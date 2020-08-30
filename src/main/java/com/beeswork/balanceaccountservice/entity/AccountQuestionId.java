package com.beeswork.balanceaccountservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountQuestionId implements Serializable {

    private Long accountId;
    private Long questionId;

}
