package com.beeswork.balanceaccountservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AccountQuestionRelId implements Serializable {

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "question_id")
    private Long questionId;

}
