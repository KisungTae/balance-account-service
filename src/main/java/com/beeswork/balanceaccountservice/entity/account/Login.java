package com.beeswork.balanceaccountservice.entity.account;

import com.beeswork.balanceaccountservice.constant.LoginType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "login")
public class Login {

    @EmbeddedId
    private LoginId loginId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account   account;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "blocked")
    private boolean blocked;

}
