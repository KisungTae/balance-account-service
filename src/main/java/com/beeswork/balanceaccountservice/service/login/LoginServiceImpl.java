package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.config.security.JWTTokenProvider;
import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.login.LoginDAO;
import com.beeswork.balanceaccountservice.dao.profile.ProfileDAO;
import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.dao.setting.SettingDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeMetaDAO;
import com.beeswork.balanceaccountservice.dao.wallet.WalletDAO;
import com.beeswork.balanceaccountservice.dto.login.LoginDTO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.account.Wallet;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.entity.login.LoginId;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.entity.setting.Setting;
import com.beeswork.balanceaccountservice.entity.swipe.SwipeMeta;
import com.beeswork.balanceaccountservice.exception.login.EmailDuplicateException;
import com.beeswork.balanceaccountservice.exception.login.EmailNotMutableException;
import com.beeswork.balanceaccountservice.exception.login.InvalidSocialLoginException;
import com.beeswork.balanceaccountservice.exception.login.LoginNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
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
    private final SettingDAO       settingDAO;
    private final ProfileDAO       profileDAO;
    private final JWTTokenProvider jwtTokenProvider;


    @Autowired
    public LoginServiceImpl(LoginDAO loginDAO,
                            AccountDAO accountDAO,
                            SwipeMetaDAO swipeMetaDAO,
                            PushTokenDAO pushTokenDAO, WalletDAO walletDAO,
                            SettingDAO settingDAO,
                            ProfileDAO profileDAO,
                            JWTTokenProvider jwtTokenProvider) {
        this.loginDAO = loginDAO;
        this.accountDAO = accountDAO;
        this.swipeMetaDAO = swipeMetaDAO;
        this.pushTokenDAO = pushTokenDAO;
        this.walletDAO = walletDAO;
        this.settingDAO = settingDAO;
        this.profileDAO = profileDAO;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginDTO login(String loginId, LoginType loginType, String email, String password) {
//        if (loginId.isEmpty()) throw new InvalidSocialLoginException();
//        loginId = loginType.concatenateLoginId(loginId);
//        Login login = loginDAO.findById(loginId);
//
//        if (login == null) {
//            login = new Login();
//        }
//        Account account = accountDAO.findById(login.getAccountId());
//        return new CreateLoginDTO(account.getId(), account.getIdentityToken());
        return null;
    }

    @Override
    @Transactional
    public LoginDTO socialLogin(String loginId, String email, LoginType loginType) {
        if (loginId.isEmpty()) throw new InvalidSocialLoginException();
        Login login = loginDAO.findById(new LoginId(loginId, loginType));

        if (login == null) {
            Date now = new Date();
            SwipeMeta swipeMeta = swipeMetaDAO.findFirst();
            Account account = new Account(now);
            login = new Login(loginId, loginType, account, email);
            Wallet wallet = new Wallet(account, swipeMeta.getMaxFreeSwipe() * swipeMeta.getSwipePoint(), now);
            Setting setting = new Setting(account);

            accountDAO.persist(account);
            loginDAO.persist(login);
            walletDAO.persist(wallet);
            settingDAO.persist(setting);

            String jwtToken = jwtTokenProvider.createToken(account.getId().toString(), account.getRoleNames());
            return new LoginDTO(account.getId(), account.getIdentityToken(), false, jwtToken);
        } else {
            Account account = accountDAO.findById(login.getAccountId());
            validateAccount(account);
            Profile profile = profileDAO.findById(account.getId());
            boolean profileExists = profile != null && profile.isEnabled();

            PushToken pushToken = pushTokenDAO.findRecent(account.getId());
            if (pushToken != null) {
                List<PushToken> pushTokens = pushTokenDAO.findAllByToken(pushToken.getToken());
                for (PushToken otherPushToken : pushTokens) {
                    if (pushToken != otherPushToken) otherPushToken.setLogin(false);
                }
                pushToken.setLogin(true);
            }

            String jwtToken = jwtTokenProvider.createToken(account.getId().toString(), account.getRoleNames());
            return new LoginDTO(account.getId(), account.getIdentityToken(), profileExists, jwtToken);
        }
    }

    @Override
    @Transactional
    public void saveEmail(UUID accountId, UUID identityToken, String email) {
        Login login = loginDAO.findByAccountId(accountId);
        if (login == null) throw new LoginNotFoundException();
        validateAccount(login.getAccount(), identityToken);

        LoginType loginType = login.getType();
        if (loginType == LoginType.NAVER || loginType == LoginType.GOOGLE)
            throw new EmailNotMutableException();

        if (!login.getEmail().equals(email)) {
            if (loginDAO.existsByEmail(email))
                throw new EmailDuplicateException();
            login.setEmail(email);
        }
    }

    @Override
    public String getEmail(UUID accountId, UUID identityToken) {
        Login login = loginDAO.findByAccountId(accountId);
        if (login == null) throw new LoginNotFoundException();
        validateAccount(login.getAccount(), identityToken);
        return login.getEmail();
    }
}
