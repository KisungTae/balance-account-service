package com.beeswork.balanceaccountservice.entity.login;

import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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
    private Account account;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "blocked")
    private boolean blocked;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

}
