package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dto.login.LoginDTO;
import com.beeswork.balanceaccountservice.dto.login.RefreshAccessTokenDTO;
import com.beeswork.balanceaccountservice.dto.login.VerifySocialLoginDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.login.InvalidSocialLoginException;
import com.beeswork.balanceaccountservice.service.login.*;
import com.beeswork.balanceaccountservice.service.pushtoken.PushTokenService;
import com.beeswork.balanceaccountservice.vm.login.LoginVM;
import com.beeswork.balanceaccountservice.vm.login.LoginWithRefreshTokenVM;
import com.beeswork.balanceaccountservice.vm.login.SocialLoginVM;
import com.beeswork.balanceaccountservice.vm.login.RefreshAccessTokenVM;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
public class LoginController extends BaseController {

    private final LoginService         loginService;
    private final PushTokenService     pushTokenService;
    private final GoogleLoginService   googleLoginService;
    private final KakaoLoginService    kakaoLoginService;
    private final NaverLoginService    naverLoginService;
    private final FacebookLoginService facebookLoginService;

    @Autowired
    public LoginController(ObjectMapper objectMapper,
                           ModelMapper modelMapper,
                           LoginService loginService,
                           PushTokenService pushTokenService,
                           GoogleLoginService googleLoginService,
                           KakaoLoginService kakaoLoginService,
                           NaverLoginService naverLoginService,
                           FacebookLoginService facebookLoginService) {
        super(objectMapper, modelMapper);
        this.loginService = loginService;
        this.pushTokenService = pushTokenService;
        this.googleLoginService = googleLoginService;
        this.kakaoLoginService = kakaoLoginService;
        this.naverLoginService = naverLoginService;
        this.facebookLoginService = facebookLoginService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> join() {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/login/social")
    public ResponseEntity<String> socialLogin(@Valid @RequestBody SocialLoginVM socialLoginVM, BindingResult bindingResult)
    throws GeneralSecurityException, IOException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        VerifySocialLoginDTO verifySocialLoginDTO = null;

        if (socialLoginVM.getLoginType() == LoginType.GOOGLE) {
            verifySocialLoginDTO = googleLoginService.verifyLogin(socialLoginVM.getLoginId(), socialLoginVM.getAccessToken());
        } else if (socialLoginVM.getLoginType() == LoginType.KAKAO) {
            verifySocialLoginDTO = kakaoLoginService.verifyLogin(socialLoginVM.getLoginId(), socialLoginVM.getAccessToken());
        } else if (socialLoginVM.getLoginType() == LoginType.NAVER) {
            verifySocialLoginDTO = naverLoginService.verifyLogin(socialLoginVM.getLoginId(), socialLoginVM.getAccessToken());
        } else if (socialLoginVM.getLoginType() == LoginType.FACEBOOK) {
            verifySocialLoginDTO = facebookLoginService.verifyLogin(socialLoginVM.getLoginId(), socialLoginVM.getAccessToken());
        }

        if (verifySocialLoginDTO == null || !verifySocialLoginDTO.isVerified()) {
            throw new InvalidSocialLoginException();
        }

        LoginDTO loginDTO = loginService.socialLogin(verifySocialLoginDTO.getSocialLoginId(),
                                                     verifySocialLoginDTO.getEmail(),
                                                     socialLoginVM.getLoginType());

        pushTokenService.savePushToken(loginDTO.getAccountId(),
                                       socialLoginVM.getPushToken(),
                                       socialLoginVM.getPushTokenType());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(loginDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginVM loginVM,
                                        BindingResult bindingResult)
    throws GeneralSecurityException, IOException {
        if (bindingResult.hasErrors()) throw new BadRequestException();

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/login/refresh-token")
    public ResponseEntity<String> loginWithRefreshToken(@RequestBody LoginWithRefreshTokenVM loginWithRefreshTokenVM,
                                                        BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        LoginDTO loginDTO = loginService.loginWithRefreshToken(loginWithRefreshTokenVM.getAccessToken(),
                                                               loginWithRefreshTokenVM.getRefreshToken());

        pushTokenService.savePushToken(loginDTO.getAccountId(),
                                       loginWithRefreshTokenVM.getPushToken(),
                                       loginWithRefreshTokenVM.getPushTokenType());

        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(loginDTO));
    }

    @PostMapping("/login/access-token/refresh")
    public ResponseEntity<String> refreshAccessToken(@RequestBody RefreshAccessTokenVM refreshAccessTokenVM,
                                                     BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        RefreshAccessTokenDTO refreshAccessTokenDTO = loginService.refreshAccessToken(refreshAccessTokenVM.getAccessToken(),
                                                                                      refreshAccessTokenVM.getRefreshToken(),
                                                                                      false);
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(refreshAccessTokenDTO));
    }
}
