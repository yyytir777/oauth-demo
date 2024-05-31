package com.example.oauthdemo.jwt;

import com.example.oauthdemo.domain.member.constant.Role;
import com.example.oauthdemo.jwt.constant.TokenType;
import com.example.oauthdemo.jwt.dto.JwtTokenDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenManager {

    @Value("${token.secret}")
    private String tokenSecret;

    @Value("${token.access-token-expiration-time}")
    private String accessTokenExpirationTime;

    @Value("${token.refresh-token-expiration-time}")
    private String refreshTokenExpirationTime;

    /**
     * accessToken, refreshToken 생성
     */
    public JwtTokenDto createJwtTokeDto(String email, Role role, Boolean enrolled) {
        Date accessTokenExpireTime = createAccessTokenExpireTime();
        Date refreshTokenExpireTime = createrefreshTokenExpireTime();

        String accessToken = createAccessToken(email, role, accessTokenExpireTime);
        String refreshToken = createRefreshToken(email, role, refreshTokenExpireTime);

        JwtTokenDto jwtTokenDto = JwtTokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpireTime(accessTokenExpireTime)
                .refreshToken(refreshToken)
                .refreshTokenExpireTime(refreshTokenExpireTime)
                .enrolled(enrolled)
                .build();
        return jwtTokenDto;
    }

    private String createAccessToken(String email, Role role, Date accessTokenExpireTime) {
        String accessToken = Jwts.builder()
                .setSubject(TokenType.ACCESS.toString())
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpireTime)
                .claim("email", email)
                .claim("role", role)
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .setHeaderParam("typ", "JWT")
                .compact();

        return accessToken;
    }

    private String createRefreshToken(String email, Role role, Date refreshTokenExpireTime) {
        String refreshToken = Jwts.builder()
                .setSubject(TokenType.REFRESH.toString())
                .setIssuedAt(new Date())
                .setExpiration(refreshTokenExpireTime)
                .claim("email", email)
                .claim("role", role)
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .setHeaderParam("typ", "JWT")
                .compact();

        return refreshToken;
    }

    private Date createAccessTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpirationTime));
    }

    private Date createrefreshTokenExpireTime() {
        return new Date(System.currentTimeMillis() + Long.parseLong(refreshTokenExpirationTime));
    }

}
