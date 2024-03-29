package com.beeswork.balanceaccountservice.vm.account;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SaveAnswersVM {

    @Size(min = 3, max = 5)
    private Map<Integer, Boolean> answers = new HashMap<>();
}
