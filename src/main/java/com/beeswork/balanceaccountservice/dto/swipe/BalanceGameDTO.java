package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import com.beeswork.balanceaccountservice.entity.question.Question;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BalanceGameDTO {

    private Long swipeId;
    private List<QuestionDTO> questions = new ArrayList<>();
}
