package com.beeswork.balanceaccountservice.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@Getter
@Setter
public class AccountVM {

    @Length(min = 1, max = 10, message = "{min} and {max}")
    private String name;



    private boolean gender;
    private List<AccountQuestionVM> accountQuestionVMs;
}
