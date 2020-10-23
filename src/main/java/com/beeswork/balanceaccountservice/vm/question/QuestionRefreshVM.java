package com.beeswork.balanceaccountservice.vm.question;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class QuestionRefreshVM extends AccountIdentityVM {

    @Size(min = 1, max = 6, message = "question.ids.size")
    private List<Long> currentQuestionIds = new ArrayList<>();
}
