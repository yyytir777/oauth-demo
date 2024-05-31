package com.example.oauthdemo.oauth.controller;

import com.example.oauthdemo.jwt.dto.JwtTokenDto;
import com.example.oauthdemo.oauth.dto.KakaoTokenDto;
import com.example.oauthdemo.oauth.dto.OAuthResponseDto;
import com.example.oauthdemo.oauth.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

//    @Qualifier("OAuthNaverService")
//    private final OAuthService oAuthNaverService;

    /** React로부터 authCode를 url로 받아와 카카오로부터 토큰을 받고 해당 토큰으로 카카오 사용자의 정보 조회
     *
     * @return OAuthResponseDto : email, name, nickname, profileImage
     */
    @GetMapping("/kakao")
    public ResponseEntity<OAuthResponseDto> getKakaoInfo(@RequestParam String code) {
        KakaoTokenDto kakaoTokenDto = oAuthService.getToken(code);
        OAuthResponseDto oAuthResponseDto = oAuthService.getInfo(kakaoTokenDto.getAccess_token());
        return ResponseEntity.ok(oAuthResponseDto);
    }

    @GetMapping("/login/kakao")
    public ResponseEntity<JwtTokenDto> kakaoLogin(HttpServletRequest request)  {
        String code = request.getParameter("code");
        KakaoTokenDto kakaoTokenDto = oAuthService.getToken(code);
        log.info("kakaoTokenDto : " + kakaoTokenDto);

        OAuthResponseDto oAuthResponseDto = oAuthService.getInfo(kakaoTokenDto.getAccess_token());
        log.info("oAuthResponseDto : " + oAuthResponseDto);

        JwtTokenDto jwtTokenDto = oAuthService.kakaoLogin(oAuthResponseDto);
        log.info("jwtTokenDto : " + jwtTokenDto);

        return ResponseEntity.ok(jwtTokenDto);
    }

//    @GetMapping("/naver")
//    public ResponseEntity<OAuthResponseDto> getNaverInfo(@PathVariable String authCode) {
//        String token = oAuthNaverService.getToken(authCode);
//        OAuthResponseDto oAuthResponseDto = oAuthNaverService.getInfo(token);
//        return ResponseEntity.ok(oAuthResponseDto);
//    }
}
