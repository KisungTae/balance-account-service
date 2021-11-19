package com.beeswork.balanceaccountservice.vm.question;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RandomQuestionVM {

    @Size(min = 1, max = 6, message = "{current.question.ids.size}")
    private List<Integer> questionIds = new ArrayList<>();
}
