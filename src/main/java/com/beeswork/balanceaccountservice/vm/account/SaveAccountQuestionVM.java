package com.beeswork.balanceaccountservice.vm.account;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SaveAccountQuestionVM extends AccountIdentityVM {

    @Size(min = 1, max = 3, message = "질문개수는 최소 {min}개 최대 {max}개 까지 입니다")
    private List<AccountQuestionVM> accountQuestionVMs = new ArrayList<>();
}
