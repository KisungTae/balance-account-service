package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.config.security.JWTTokenProvider;
import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.login.LoginDAO;
import com.beeswork.balanceaccountservice.dao.login.RefreshTokenDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.dao.setting.PushSettingDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeMetaDAO;
import com.beeswork.balanceaccountservice.dao.wallet.WalletDAO;
import com.beeswork.balanceaccountservice.dto.login.LoginDTO;
import com.beeswork.balanceaccountservice.dto.login.RefreshAccessTokenDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.Wallet;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.entity.login.LoginId;
import com.beeswork.balanceaccountservice.entity.login.RefreshToken;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.entity.setting.PushSetting;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeMeta;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.jwt.InvalidRefreshTokenException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class LoginServiceImpl extends BaseServiceImpl implements LoginService {

    private final LoginDAO         loginDAO;
    private final AccountDAO       accountDAO;
    private final SwipeMetaDAO     swipeMetaDAO;
    private final PushTokenDAO     pushTokenDAO;
    private final WalletDAO        walletDAO;
    private final PushSettingDAO   pushSettingDAO;
    private final ProfileDAO       profileDAO;
    private final RefreshTokenDAO  refreshTokenDAO;
    private final JWTTokenProvider jwtTokenProvider;


    @Autowired
    public LoginServiceImpl(LoginDAO loginDAO,
                            AccountDAO accountDAO,
                            SwipeMetaDAO swipeMetaDAO,
                            PushTokenDAO pushTokenDAO, WalletDAO walletDAO,
                            PushSettingDAO pushSettingDAO,
                            ProfileDAO profileDAO,
                            RefreshTokenDAO refreshTokenDAO,
                            JWTTokenProvider jwtTokenProvider) {
        this.loginDAO = loginDAO;
        this.accountDAO = accountDAO;
        this.swipeMetaDAO = swipeMetaDAO;
        this.pushTokenDAO = pushTokenDAO;
        this.walletDAO = walletDAO;
        this.pushSettingDAO = pushSettingDAO;
        this.profileDAO = profileDAO;
        this.refreshTokenDAO = refreshTokenDAO;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginDTO login(String loginId, LoginType loginType, String email, String password) {
        return null;
    }

    @Override
    @Transactional
    public LoginDTO socialLogin(String loginId, String email, LoginType loginType) {
        Login login = loginDAO.findById(new LoginId(loginId, loginType));
        if (login == null) return loginWithNewAccount(loginId, email, loginType);
        else return loginWithExistingAccount(login);
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
        return new LoginDTO(account.getId(), false, accessToken, newRefreshToken, email);
    }

    private LoginDTO loginWithExistingAccount(Login login) {
        Account account = accountDAO.findById(login.getAccountId());
        if (account == null) throw new AccountNotFoundException();
        account.validate();
        updatePushToken(account.getId());

        RefreshToken refreshToken = new RefreshToken(account);
        String newRefreshToken = createNewRefreshToken(account, refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(account.getId().toString(), account.getRoleNames());

        Profile profile = profileDAO.findById(account.getId());
        boolean profileExists = profile != null && profile.isEnabled();
        Boolean gender = profileExists ? profile.isGender() : null;
        return new LoginDTO(account.getId(), profileExists, accessToken, newRefreshToken, login.getEmail(), gender);
    }

    private void updatePushToken(UUID accountId) {
        PushToken pushToken = pushTokenDAO.findRecent(accountId);
        if (pushToken != null) {
            List<PushToken> pushTokens = pushTokenDAO.findAllByToken(pushToken.getToken());
            for (PushToken otherPushToken : pushTokens) {
                if (pushToken != otherPushToken) otherPushToken.setLogin(false);
            }
            pushToken.setLogin(true);
        }
    }

    @Override
    @Transactional
    public RefreshAccessTokenDTO refreshAccessToken(String accessToken, String refreshToken, boolean includeAccountId) {
        Jws<Claims> refreshTokenJws = jwtTokenProvider.parseJWTToken(refreshToken);
        jwtTokenProvider.validateJWTToken(refreshTokenJws);
        UUID refreshTokenUserName = jwtTokenProvider.getUserName(refreshTokenJws);

        Jws<Claims> accessTokenJws = jwtTokenProvider.parseJWTToken(accessToken);
        UUID accessTokenUserName = jwtTokenProvider.getUserName(accessTokenJws);
        if (!refreshTokenUserName.equals(accessTokenUserName)) throw new InvalidRefreshTokenException();

        Account account = findValidAccountFromJWTToken(refreshTokenUserName);
        validateRefreshTokenKey(account.getId(), refreshTokenJws);
        String newRefreshToken = null;
        if (jwtTokenProvider.shouldReissueRefreshToken(refreshTokenJws)) {
            newRefreshToken = createNewRefreshToken(account, new RefreshToken(account));
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(account.getId().toString(), account.getRoleNames());

        RefreshAccessTokenDTO refreshAccessTokenDTO = new RefreshAccessTokenDTO(newAccessToken, newRefreshToken);
        if (includeAccountId) refreshAccessTokenDTO.setAccountId(account.getId());
        return refreshAccessTokenDTO;
    }

    @Override
    @Transactional
    public LoginDTO loginWithRefreshToken(String accessToken, String refreshToken) {
        RefreshAccessTokenDTO refreshAccessTokenDTO = refreshAccessToken(accessToken, refreshToken, true);
        Profile profile = profileDAO.findById(refreshAccessTokenDTO.getAccountId());
        boolean profileExists = profile != null && profile.isEnabled();
        Boolean gender = profileExists ? profile.isGender() : null;

        return new LoginDTO(refreshAccessTokenDTO.getAccountId(),
                            profileExists,
                            refreshAccessTokenDTO.getAccessToken(),
                            refreshAccessTokenDTO.getRefreshToken(),
                            gender);
    }


    private void validateRefreshTokenKey(UUID accountId, Jws<Claims> jws) {
        UUID refreshTokenKey = jwtTokenProvider.getRefreshTokenKey(jws);
        if (refreshTokenKey == null) throw new InvalidRefreshTokenException();
        if (!refreshTokenDAO.existsByAccountIdAndKey(accountId, refreshTokenKey))
            throw new InvalidRefreshTokenException();
    }

    private Account findValidAccountFromJWTToken(UUID userName) {
        if (userName == null) throw new AccountNotFoundException();
        Account account = accountDAO.findById(userName);
        if (account == null) throw new AccountNotFoundException();
        account.validate();
        return account;
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
