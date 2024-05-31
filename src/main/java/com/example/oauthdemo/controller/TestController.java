package com.example.oauthdemo.controller;

import com.example.oauthdemo.oauth.dto.KakaoTokenDto;
import com.example.oauthdemo.oauth.dto.OAuthResponseDto;
import com.example.oauthdemo.oauth.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TestController {

    public final OAuthService oAuthService;

    @GetMapping("/content")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }

    @GetMapping("/oauth/kakao/callback")
    public ResponseEntity<OAuthResponseDto> test(HttpServletRequest request) {
        String code = request.getParameter("code");
        KakaoTokenDto kakaoTokenDto = oAuthService.getToken(code);
        log.info("kakaoTokenDto : " + kakaoTokenDto);
        OAuthResponseDto oAuthResponseDto = oAuthService.getInfo(kakaoTokenDto.getAccess_token());
        log.info("oAuthResponseDto : " + oAuthResponseDto);
        return ResponseEntity.ok(oAuthResponseDto);
    }
}
