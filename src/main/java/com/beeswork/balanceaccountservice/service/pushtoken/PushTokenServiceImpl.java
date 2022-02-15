package com.beeswork.balanceaccountservice.service.pushtoken;

import com.beeswork.balanceaccountservice.constant.PushTokenType;
import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.pushtoken.PushTokenDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushTokenId;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class PushTokenServiceImpl extends BaseServiceImpl implements PushTokenService {

    private final PushTokenDAO pushTokenDAO;
    private final AccountDAO   accountDAO;

    @Autowired
    public PushTokenServiceImpl(PushTokenDAO pushTokenDAO,
                                AccountDAO accountDAO) {
        this.pushTokenDAO = pushTokenDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    @Transactional
    public void savePushToken(UUID accountId, String token, PushTokenType type) {
        if (StringUtils.isBlank(token)) {
            return;
        }
        PushToken pushToken = pushTokenDAO.findById(new PushTokenId(accountId, type));
        Date now = new Date();
        if (pushToken == null) {
            Account account = accountDAO.findById(accountId, false);
            pushToken = new PushToken(account, type, token, now);
            pushTokenDAO.persist(pushToken);
        } else {
            pushToken.setActive(true);
            pushToken.setToken(token);
            pushToken.setUpdatedAt(now);
        }

        List<PushToken> pushTokens = pushTokenDAO.findAllBy(pushToken.getToken(), pushToken.getType());
        for (PushToken otherPushToken : pushTokens) {
            if (pushToken != otherPushToken) {
                otherPushToken.setActive(false);
            }
        }
    }
}
