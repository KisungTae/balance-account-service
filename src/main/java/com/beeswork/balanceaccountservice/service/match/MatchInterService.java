package com.beeswork.balanceaccountservice.service.match;

import java.util.UUID;

public interface MatchInterService {

    boolean existsById(UUID matcherId, UUID matchedId);
}
