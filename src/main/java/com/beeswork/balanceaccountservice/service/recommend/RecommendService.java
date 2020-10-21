package com.beeswork.balanceaccountservice.service.recommend;

import com.beeswork.balanceaccountservice.dto.account.CardDTO;
import com.beeswork.balanceaccountservice.dto.recommend.RecommendDTO;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;

import java.util.List;

public interface RecommendService {

    List<CardDTO> recommend(RecommendDTO recommendDTO);
}
