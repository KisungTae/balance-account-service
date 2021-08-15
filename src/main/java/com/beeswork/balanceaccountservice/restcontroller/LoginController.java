package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.config.security.JWTTokenProvider;
import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dto.login.LoginDTO;
import com.beeswork.balanceaccountservice.dto.login.RefreshAccessTokenDTO;
import com.beeswork.balanceaccountservice.dto.login.VerifyLoginDTO;
import com.beeswork.balanceaccountservice.exception.BadRequestException;
import com.beeswork.balanceaccountservice.exception.login.InvalidSocialLoginException;
import com.beeswork.balanceaccountservice.response.EmptyJsonResponse;
import com.beeswork.balanceaccountservice.service.login.*;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import com.beeswork.balanceaccountservice.vm.account.SaveEmailVM;
import com.beeswork.balanceaccountservice.vm.login.LoginVM;
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
    private final GoogleLoginService   googleLoginService;
    private final KakaoLoginService    kakaoLoginService;
    private final NaverLoginService    naverLoginService;
    private final FacebookLoginService facebookLoginService;
    private final JWTTokenProvider     jwtTokenProvider;

    @Autowired
    public LoginController(ObjectMapper objectMapper,
                           ModelMapper modelMapper,
                           LoginService loginService,
                           GoogleLoginService googleLoginService,
                           KakaoLoginService kakaoLoginService,
                           NaverLoginService naverLoginService,
                           FacebookLoginService facebookLoginService,
                           JWTTokenProvider jwtTokenProvider) {
        super(objectMapper, modelMapper);
        this.loginService = loginService;
        this.googleLoginService = googleLoginService;
        this.kakaoLoginService = kakaoLoginService;
        this.naverLoginService = naverLoginService;
        this.facebookLoginService = facebookLoginService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login/email")
    public ResponseEntity<String> saveEmail(@Valid @RequestBody SaveEmailVM saveEmailVM,
                                            BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) super.fieldExceptionResponse(bindingResult);
        loginService.saveEmail(saveEmailVM.getAccountId(), saveEmailVM.getIdentityToken(), saveEmailVM.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(new EmptyJsonResponse()));
    }

    @GetMapping("/login/email")
    public ResponseEntity<String> getEmail(@Valid @ModelAttribute AccountIdentityVM accountIdentityVM,
                                           BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        String email = loginService.getEmail(accountIdentityVM.getAccountId(), accountIdentityVM.getIdentityToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(email));
    }

    @PostMapping("/join")
    public ResponseEntity<String> join() {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/login/social")
    public ResponseEntity<String> socialLogin(@Valid @RequestBody SocialLoginVM socialLoginVM,
                                              BindingResult bindingResult)
    throws GeneralSecurityException, IOException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        VerifyLoginDTO verifyLoginDTO = null;

        if (socialLoginVM.getLoginType() == LoginType.GOOGLE)
            verifyLoginDTO = googleLoginService.verifyLogin(socialLoginVM.getLoginId(), socialLoginVM.getAccessToken());
        else if (socialLoginVM.getLoginType() == LoginType.KAKAO)
            verifyLoginDTO = kakaoLoginService.verifyLogin(socialLoginVM.getLoginId(), socialLoginVM.getAccessToken());
        else if (socialLoginVM.getLoginType() == LoginType.NAVER)
            verifyLoginDTO = naverLoginService.verifyLogin(socialLoginVM.getLoginId(), socialLoginVM.getAccessToken());
        else if (socialLoginVM.getLoginType() == LoginType.FACEBOOK)
            verifyLoginDTO = facebookLoginService.verifyLogin(socialLoginVM.getLoginId(), socialLoginVM.getAccessToken());

        if (verifyLoginDTO == null) throw new InvalidSocialLoginException();
        LoginDTO loginDTO = loginService.socialLogin(verifyLoginDTO.getSocialLoginId(),
                                                     verifyLoginDTO.getEmail(),
                                                     socialLoginVM.getLoginType());

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
    public ResponseEntity<String> loginWithRefreshToken(@Valid @RequestBody RefreshAccessTokenVM refreshAccessTokenVM,
                                                        BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        LoginDTO loginDTO = loginService.loginWithRefreshToken(refreshAccessTokenVM.getAccountId(), refreshAccessTokenVM.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(loginDTO));
    }

    @PostMapping("/login/access-token/refresh")
    public ResponseEntity<String> refreshAccessToken(@Valid @RequestBody RefreshAccessTokenVM refreshAccessTokenVM,
                                                     BindingResult bindingResult) throws JsonProcessingException {
        if (bindingResult.hasErrors()) throw new BadRequestException();
        RefreshAccessTokenDTO refreshAccessTokenDTO = loginService.refreshAccessToken(refreshAccessTokenVM.getAccountId(),
                                                                                      refreshAccessTokenVM.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(refreshAccessTokenDTO));
    }
}
