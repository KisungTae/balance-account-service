package com.beeswork.balanceaccountservice.dto.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    private int id;
    private String description;
    private String topOption;
    private String bottomOption;
    private Boolean answer;

    public QuestionDTO(int id, String description, String topOption, String bottomOption) {
        this.id = id;
        this.description = description;
        this.topOption = topOption;
        this.bottomOption = bottomOption;
    }
}
