package com.beeswork.balanceaccountservice.dao.match;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.entity.match.UnmatchAudit;

import java.util.UUID;

public interface UnmatchAuditDAO extends BaseDAO<UnmatchAudit> {
    boolean existsBy(UUID swiperId, UUID swipedId);
}
