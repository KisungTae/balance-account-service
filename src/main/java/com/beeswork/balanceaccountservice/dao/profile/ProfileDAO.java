package com.beeswork.balanceaccountservice.dao.profile;

import com.beeswork.balanceaccountservice.dao.base.BaseDAO;
import com.beeswork.balanceaccountservice.dto.profile.CardDTO;
import com.beeswork.balanceaccountservice.dto.profile.ProfileDTO;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.UUID;

public interface ProfileDAO extends BaseDAO<Profile> {
    Profile findById(UUID accountId);
    Profile findByIdWithLock(UUID accountId);
    CardDTO findCardDTO(UUID swipedId, Point pivot);
    boolean existsById(UUID accountId);
    List<CardDTO> findCardDTOs(int distance, int minAge, int maxAge, boolean gender, int limit, int offset, Point pivot);
}
