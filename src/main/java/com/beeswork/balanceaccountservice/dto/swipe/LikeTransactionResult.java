package com.beeswork.balanceaccountservice.dto.swipe;

import com.beeswork.balanceaccountservice.entity.question.Question;
import com.beeswork.balanceaccountservice.entity.swipe.Swipe;
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
public class LikeTransactionResult {
    private List<Question> questions = new ArrayList<>();
    private int            point;
    private Swipe          swipe;
}
