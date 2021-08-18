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
import com.beeswork.balanceaccountservice.exception.account.AccountBlockedException;
import com.beeswork.balanceaccountservice.exception.account.AccountDeletedException;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.exception.login.*;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import com.beeswork.balanceaccountservice.util.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
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

        Account account = new Account(UUID.randomUUID(), now);
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
        return new LoginDTO(account.getId(), account.getIdentityToken(), false, accessToken, newRefreshToken, email);
    }

    private LoginDTO loginWithExistingAccount(Login login) {
        Account account = accountDAO.findById(login.getAccountId());
        validateAccount(account);
        updatePushToken(account.getId());

        RefreshToken refreshToken = refreshTokenDAO.findByAccountId(account.getId());
        if (refreshToken == null)
            refreshToken = new RefreshToken(account);

        String newRefreshToken = createNewRefreshToken(account, refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(account.getId().toString(), account.getRoleNames());
        boolean profileExists = profileExists(account.getId());
        return new LoginDTO(account.getId(), account.getIdentityToken(), profileExists, accessToken, newRefreshToken, login.getEmail());
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

    private boolean profileExists(UUID accountId) {
        Profile profile = profileDAO.findById(accountId);
        return profile != null && profile.isEnabled();
    }

    @Override
    @Transactional
    public RefreshAccessTokenDTO refreshAccessToken(UUID accountId, String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) throw new RefreshTokenExpiredException();
        Account account = findValidAccountFromToken(accountId, refreshToken);
        RefreshToken accountRefreshToken = findValidRefreshToken(accountId, refreshToken);
        String newRefreshToken = createNewRefreshToken(account, accountRefreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(account.getId().toString(), account.getRoleNames());
        return new RefreshAccessTokenDTO(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public LoginDTO loginWithRefreshToken(UUID accountId, String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) throw new RefreshTokenExpiredException();
        Account account = findValidAccountFromToken(accountId, refreshToken);
        RefreshToken accountRefreshToken = findValidRefreshToken(accountId, refreshToken);
        boolean profileExists = profileExists(account.getId());
        String newRefreshToken = createNewRefreshToken(account, accountRefreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(account.getId().toString(), account.getRoleNames());
        return new LoginDTO(account.getId(), account.getIdentityToken(), profileExists, newAccessToken, newRefreshToken);
    }

    private Account findValidAccountFromToken(UUID accountId, String token) {
        String userName = jwtTokenProvider.getUserName(token);
        UUID userNameUUID = Convert.toUUIDOrThrow(userName, new AccountNotFoundException());
        if (!accountId.equals(userNameUUID)) throw new AccountNotFoundException();
        Account account = accountDAO.findById(accountId);
        validateAccount(account);
        return account;
    }

    private RefreshToken findValidRefreshToken(UUID accountId, String refreshToken) {
        RefreshToken accountRefreshToken = refreshTokenDAO.findByAccountId(accountId);
        if (accountRefreshToken == null) throw new RefreshTokenNotFoundException();

        String refreshTokenKey = jwtTokenProvider.getRefreshTokenKey(refreshToken);
        if (!accountRefreshToken.getKey().toString().equals(refreshTokenKey))
            throw new RefreshTokenNotFoundException();

        return accountRefreshToken;
    }

    private String createNewRefreshToken(Account account, RefreshToken refreshToken) {
        UUID refreshTokenKey = UUID.randomUUID();
        refreshToken.setKey(refreshTokenKey);
        refreshToken.setUpdatedAt(new Date());
        refreshTokenDAO.persist(refreshToken);
        return jwtTokenProvider.createRefreshToken(account.getId().toString(), refreshTokenKey.toString());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public UserDetails loadUserByUsername(String userName, String identityToken) {
        UUID userNameUUID = Convert.toUUIDOrThrow(userName, new AccountNotFoundException());
        UUID identityTokenUUID = Convert.toUUIDOrThrow(identityToken, new AccountNotFoundException());
        Account account = accountDAO.findById(userNameUUID);
        validateAccount(account, identityTokenUUID);
        return account;
    }

    private void validateAccount(Account account, UUID identityToken) {
        validateAccount(account);
        if (!account.getIdentityToken().equals(identityToken)) throw new AccountNotFoundException();
    }

    private void validateAccount(Account account) {
        if (account == null) throw new AccountNotFoundException();
        if (account.isBlocked()) throw new AccountBlockedException();
        if (account.isDeleted()) throw new AccountDeletedException();
    }
}
