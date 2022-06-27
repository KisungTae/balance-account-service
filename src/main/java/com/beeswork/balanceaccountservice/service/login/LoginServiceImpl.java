package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.config.properties.AWSProperties;
import com.beeswork.balanceaccountservice.config.security.JWTTokenProvider;
import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.login.LoginDAO;
import com.beeswork.balanceaccountservice.dao.login.RefreshTokenDAO;
import com.beeswork.balanceaccountservice.dao.photo.PhotoDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dao.setting.PushSettingDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeMetaDAO;
import com.beeswork.balanceaccountservice.dao.wallet.WalletDAO;
import com.beeswork.balanceaccountservice.dto.login.LoginDTO;
import com.beeswork.balanceaccountservice.dto.login.RefreshAccessTokenDTO;
import com.beeswork.balanceaccountservice.dto.photo.PhotoDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.Wallet;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.entity.login.LoginId;
import com.beeswork.balanceaccountservice.entity.login.RefreshToken;
import com.beeswork.balanceaccountservice.entity.photo.Photo;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.setting.PushSetting;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeMeta;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.jwt.ExpiredJWTException;
import com.beeswork.balanceaccountservice.exception.jwt.InvalidRefreshTokenException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.service.pushtoken.PushTokenService;
import com.beeswork.balanceaccountservice.service.security.UserDetailService;
import com.beeswork.balanceaccountservice.util.Convert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class LoginServiceImpl extends BaseServiceImpl implements LoginService {

    private final LoginDAO          loginDAO;
    private final AccountDAO        accountDAO;
    private final SwipeMetaDAO      swipeMetaDAO;
    private final PhotoDAO          photoDAO;
    private final WalletDAO         walletDAO;
    private final PushSettingDAO    pushSettingDAO;
    private final ProfileDAO        profileDAO;
    private final RefreshTokenDAO   refreshTokenDAO;
    private final JWTTokenProvider  jwtTokenProvider;
    private final PushTokenService  pushTokenService;
    private final AWSProperties     awsProperties;
    private final UserDetailService userDetailService;
    private final ModelMapper       modelMapper;


    @Autowired
    public LoginServiceImpl(LoginDAO loginDAO,
                            AccountDAO accountDAO,
                            SwipeMetaDAO swipeMetaDAO,
                            PhotoDAO photoDAO,
                            WalletDAO walletDAO,
                            PushSettingDAO pushSettingDAO,
                            ProfileDAO profileDAO,
                            RefreshTokenDAO refreshTokenDAO,
                            JWTTokenProvider jwtTokenProvider,
                            PushTokenService pushTokenService,
                            AWSProperties awsProperties,
                            UserDetailService userDetailsService,
                            ModelMapper modelMapper) {
        this.loginDAO = loginDAO;
        this.accountDAO = accountDAO;
        this.swipeMetaDAO = swipeMetaDAO;
        this.photoDAO = photoDAO;
        this.walletDAO = walletDAO;
        this.pushSettingDAO = pushSettingDAO;
        this.profileDAO = profileDAO;
        this.refreshTokenDAO = refreshTokenDAO;
        this.jwtTokenProvider = jwtTokenProvider;
        this.pushTokenService = pushTokenService;
        this.awsProperties = awsProperties;
        this.userDetailService = userDetailsService;
        this.modelMapper = modelMapper;
    }

    @Override
    public LoginDTO login(String loginId, LoginType loginType, String email, String password) {
        return null;
    }

    @Override
    @Transactional
    public LoginDTO socialLogin(String loginId, String email, LoginType loginType, String pushToken, PushTokenType pushTokenType) {
        Login login = loginDAO.findById(new LoginId(loginId, loginType));
        LoginDTO loginDTO;
        if (login == null) {
            loginDTO = loginWithNewAccount(loginId, email, loginType);
        } else {
            loginDTO = loginWithExistingAccount(login);
        }
        pushTokenService.savePushToken(loginDTO.getAccountId(), pushToken, pushTokenType);
        return loginDTO;
    }

    private LoginDTO loginWithNewAccount(String loginId, String email, LoginType loginType) {
        Date now = new Date();
        SwipeMeta swipeMeta = swipeMetaDAO.findFirst();

        Account account = new Account(now);
        Login login = new Login(loginId, loginType, account, email);
        Wallet wallet = new Wallet(account, swipeMeta.getMaxFreeSwipe() * swipeMeta.getSwipePoint(), now);
        PushSetting pushSetting = new PushSetting(account);

        accountDAO.persist(account);
        loginDAO.persist(login);
        walletDAO.persist(wallet);
        pushSettingDAO.persist(pushSetting);

        RefreshToken refreshToken = new RefreshToken(account);
        String newRefreshToken = createNewRefreshToken(account, refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(account.getId().toString(), account.getRoleNames());
        return new LoginDTO(account.getId(), false, accessToken, newRefreshToken, email, awsProperties.getPhotoURLDomain(), null);
    }

    private LoginDTO loginWithExistingAccount(Login login) {
        Account account = accountDAO.findById(login.getAccountId(), false);
        if (account == null) throw new AccountNotFoundException();
        account.validate();

        RefreshToken refreshToken = new RefreshToken(account);
        String newRefreshToken = createNewRefreshToken(account, refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(account.getId().toString(), account.getRoleNames());

        Profile profile = profileDAO.findById(account.getId(), false);
        boolean profileExists = profile != null && profile.isEnabled();

        Photo profilePhoto = photoDAO.getProfilePhotoBy(account.getId());
        PhotoDTO profilePhotoDTO = modelMapper.map(profilePhoto, PhotoDTO.class);

        return new LoginDTO(account.getId(),
                            profileExists,
                            accessToken,
                            newRefreshToken,
                            login.getEmail(),
                            awsProperties.getPhotoURLDomain(),
                            profilePhotoDTO);
    }

    @Override
    @Transactional
    public RefreshAccessTokenDTO refreshAccessToken(String accessToken, String refreshToken) {
        Jws<Claims> refreshTokenJws = jwtTokenProvider.parseJWTToken(refreshToken);
        jwtTokenProvider.validateJWTToken(refreshTokenJws);
        UUID refreshTokenUserName = Convert.toUUID(jwtTokenProvider.getUserName(refreshTokenJws));
        if (refreshTokenUserName == null) {
            throw new InvalidRefreshTokenException();
        }

        // parsJWTToken throws ExpiredJWTException
        UUID accessTokenUserName = null;
        try {
            Jws<Claims> accessTokenJws = jwtTokenProvider.parseJWTToken(accessToken);
            accessTokenUserName = Convert.toUUID(jwtTokenProvider.getUserName(accessTokenJws));
        } catch (ExpiredJWTException e) {
            Claims claims = e.getClaims();
            if (claims != null) {
                accessTokenUserName = Convert.toUUID(claims.getSubject());
            }
        }
        if (!refreshTokenUserName.equals(accessTokenUserName)) {
            throw new InvalidRefreshTokenException();
        }

        Account account = (Account) userDetailService.loadValidUserByUsername(refreshTokenUserName);
        validateRefreshTokenKey(account.getId(), refreshTokenJws);
        String newRefreshToken = null;
        if (jwtTokenProvider.shouldReissueRefreshToken(refreshTokenJws)) {
            newRefreshToken = createNewRefreshToken(account, new RefreshToken(account));
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(account.getId().toString(), account.getRoleNames());

        RefreshAccessTokenDTO refreshAccessTokenDTO = new RefreshAccessTokenDTO(newAccessToken, newRefreshToken);
        refreshAccessTokenDTO.setAccountId(account.getId());
        return refreshAccessTokenDTO;
    }

    @Override
    @Transactional
    public LoginDTO loginWithRefreshToken(String accessToken, String refreshToken, String pushToken, PushTokenType pushTokenType) {
        RefreshAccessTokenDTO refreshAccessTokenDTO = refreshAccessToken(accessToken, refreshToken);
        pushTokenService.savePushToken(refreshAccessTokenDTO.getAccountId(), pushToken, pushTokenType);
        Profile profile = profileDAO.findById(refreshAccessTokenDTO.getAccountId(), false);
        boolean profileExists = profile != null && profile.isEnabled();
        return new LoginDTO(refreshAccessTokenDTO.getAccountId(),
                            profileExists,
                            refreshAccessTokenDTO.getAccessToken(),
                            refreshAccessTokenDTO.getRefreshToken(),
                            awsProperties.getPhotoURLDomain());
    }


    private void validateRefreshTokenKey(UUID accountId, Jws<Claims> jws) {
        UUID refreshTokenKey = jwtTokenProvider.getRefreshTokenKey(jws);
        if (refreshTokenKey == null) {
            throw new InvalidRefreshTokenException();
        }
        if (!refreshTokenDAO.existsByAccountIdAndKey(accountId, refreshTokenKey)) {
            throw new InvalidRefreshTokenException();
        }
    }

    private String createNewRefreshToken(Account account, RefreshToken refreshToken) {
        UUID refreshTokenKey = UUID.randomUUID();
        Date issuedAt = new Date();
        refreshToken.setKey(refreshTokenKey);
        refreshToken.setUpdatedAt(issuedAt);
        refreshTokenDAO.persist(refreshToken);
        return jwtTokenProvider.createRefreshToken(account.getId().toString(), refreshTokenKey.toString(), issuedAt);
    }
}
