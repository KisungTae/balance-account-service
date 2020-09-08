package com.beeswork.balanceaccountservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AccountQuestionId implements Serializable {

    private UUID accountId;
    private Long questionId;

}
