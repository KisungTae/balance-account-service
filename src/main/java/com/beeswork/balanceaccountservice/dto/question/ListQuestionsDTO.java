package com.beeswork.balanceaccountservice.dto.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ListQuestionsDTO {
    private int point;
    private List<QuestionDTO> questionDTOs = new ArrayList<>();
}
