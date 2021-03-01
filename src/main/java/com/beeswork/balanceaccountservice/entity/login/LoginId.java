package com.beeswork.balanceaccountservice.entity.login;

import com.beeswork.balanceaccountservice.constant.LoginType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class LoginId implements Serializable {

    private String id;

    @Enumerated
    private LoginType type;

}
