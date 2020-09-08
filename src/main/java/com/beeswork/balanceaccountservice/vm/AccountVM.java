package com.beeswork.balanceaccountservice.vm;

import com.beeswork.balanceaccountservice.constant.RegexPattern;
import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class AccountVM {

//  for indentity checks
    @ValidUUID
    private String id;

    @NotEmpty(message = "이름을 입력해주세요")
    @Length(min = 1, max = 50, message = "이름은 최소 {min} 글자 그리고 최대 {max} 글자로 작성해주세요")
    private String name;

    private int birth;

    @NotEmpty(message = "자기소개를 입력해주세요")
    @Length(min = 1, max = 500, message = "자기소개는 최소 {min} 글자 그리고 최대 {max} 글자로 작성해주세요")
    private String about;

    private boolean gender;

    private double latitude;

    private double longitude;

    private List<AccountQuestionVM> accountQuestionVMs = new ArrayList<>();
}
