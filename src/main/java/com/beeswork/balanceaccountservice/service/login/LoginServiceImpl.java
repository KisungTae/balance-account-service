package com.beeswork.balanceaccountservice.service.login;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.dao.login.LoginDAO;
import com.beeswork.balanceaccountservice.entity.login.Login;
import com.beeswork.balanceaccountservice.exception.login.EmailDuplicateException;
import com.beeswork.balanceaccountservice.exception.login.EmailNotMutableException;
import com.beeswork.balanceaccountservice.exception.login.LoginNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LoginServiceImpl extends BaseServiceImpl implements LoginService {

    private final LoginDAO loginDAO;

    @Autowired
    public LoginServiceImpl(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    @Override
    @Transactional
    public void saveEmail(UUID accountId, UUID identityToken, String email) {
        Login login = loginDAO.findByAccountId(accountId);
        if (login == null) throw new LoginNotFoundException();
        validateAccount(login.getAccount(), identityToken);

        LoginType loginType = login.getLoginId().getType();
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
