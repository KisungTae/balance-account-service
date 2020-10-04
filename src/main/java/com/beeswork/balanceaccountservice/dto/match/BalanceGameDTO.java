package com.beeswork.balanceaccountservice.dto.match;


import com.beeswork.balanceaccountservice.dto.question.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceGameDTO {

    private Long              swipeId;
    private List<QuestionDTO> questions = new ArrayList<>();
}
