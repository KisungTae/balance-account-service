package com.beeswork.balanceaccountservice.entity.login;

import com.beeswork.balanceaccountservice.constant.LoginType;
import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "login")
public class Login {

    @EmbeddedId
    private LoginId loginId;

    @Column(name = "account_id", insertable = false, updatable = false)
    private UUID accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "blocked")
    private boolean blocked;

    public Login(String id, LoginType type, Account account, String email) {
        this.loginId = new LoginId(id, type);
        this.account = account;
        this.email = email;
    }

    public LoginType getType() {
        return this.loginId.getType();
    }
}
