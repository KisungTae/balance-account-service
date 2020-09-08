package com.beeswork.balanceaccountservice.dto.question;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class QuestionDTO {
    private Long id;
    private String description;
    private String topOption;
    private String bottomOption;
    private boolean selected;
}
