package com.beeswork.balanceaccountservice.service.count;


import com.beeswork.balanceaccountservice.dto.count.CountTabDTO;
import java.util.List;
import java.util.UUID;


public interface CountService {

    List<CountTabDTO> countTabs(UUID accountId);
}
