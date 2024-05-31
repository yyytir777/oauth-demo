package com.example.oauthdemo.oauth.service;

import com.example.oauthdemo.domain.member.constant.MemberType;
import com.example.oauthdemo.domain.member.entity.Member;
import com.example.oauthdemo.domain.member.constant.Role;
import com.example.oauthdemo.domain.member.repository.MemberRepository;
import com.example.oauthdemo.jwt.TokenManager;
import com.example.oauthdemo.jwt.dto.JwtTokenDto;
import com.example.oauthdemo.oauth.dto.KakaoTokenDto;
import com.example.oauthdemo.oauth.dto.OAuthResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthService {

    private final MemberRepository memberRepository;
    private final TokenManager tokenManager;

    @Value("${oauth.kakao.client_id}")
    private String client_id;

    @Value("${oauth.kakao.redirect_uri}")
    private String redirect_uri;

    public KakaoTokenDto getToken(String authCode) {

        log.info("code : " + authCode);
        // Http Request Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // Http Request Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client_id);
        params.add("redirect_uri", redirect_uri);
        params.add("code", authCode);
        
        // header + body로 Http 객체 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
        
        // Http 요청 전송 후 응답 받아오기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        //Json parsing -> kakaoTokenDto
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoTokenDto kakaoTokenDto = null;

        try {
            kakaoTokenDto= objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoTokenDto;
    }

    public OAuthResponseDto getInfo(String kakaoAccessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/json");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<String> accountInfoResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                accountInfoRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OAuthResponseDto oAuthResponseDto = null;

        try {
            oAuthResponseDto = objectMapper.readValue(accountInfoResponse.getBody(), OAuthResponseDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oAuthResponseDto;
    }

    /**
     * 해당 이메일로 멤버 조회
     * 없으면 멤버 생성 후 jwt - accessToken, refreshToken 생성
     * 있으면 jwt - accessToken, refreshToken 생성 -> refreshToken은 업데이트
     */
    public JwtTokenDto kakaoLogin(OAuthResponseDto oAuthResponseDto) {
        String email = oAuthResponseDto.getKakaoAccount().getEmail();

        Optional<Member> member = memberRepository.findByEmail(email);
        JwtTokenDto jwtTokenDto = null;

        if (member.isEmpty()) {
            Member newMember = Member.builder()
                    .name(oAuthResponseDto.getKakaoAccount().getProfile().getNickname())
                    .email(oAuthResponseDto.getKakaoAccount().getEmail())
                    .memberType(MemberType.KAKAO)
                    .profileUrl(oAuthResponseDto.getKakaoAccount().getProfile().getThumbnailImageUrl())
                    .role(Role.USER)
                    .build();

            memberRepository.save(newMember);

            jwtTokenDto = tokenManager.createJwtTokeDto(email, Role.USER, false);
        } else if(member.get().getMemberType() == MemberType.KAKAO){
            Member oauthMember = member.get();

            jwtTokenDto = tokenManager.createJwtTokeDto(oauthMember.getEmail(), Role.USER, true);
        }

        return jwtTokenDto;
    }
}
