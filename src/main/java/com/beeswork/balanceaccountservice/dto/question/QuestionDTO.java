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

    private long id;
    private String description;
    private String topOption;
    private String bottomOption;
    private boolean selected;

    public QuestionDTO(String description, String topOption, String bottomOption, boolean selected) {
        this.description = description;
        this.topOption = topOption;
        this.bottomOption = bottomOption;
        this.selected = selected;
    }
}
