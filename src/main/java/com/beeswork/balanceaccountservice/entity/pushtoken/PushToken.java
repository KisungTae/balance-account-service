package com.beeswork.balanceaccountservice.entity.pushtoken;


import com.beeswork.balanceaccountservice.constant.PushTokenType;
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
@Table(name = "push_token")
public class PushToken {

    @EmbeddedId
    private PushTokenId pushTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    private Account account;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public PushToken(Account account, PushTokenType type, String token, Date createdAt) {
        this.pushTokenId = new PushTokenId(account.getId(), type);
        this.account = account;
        this.token = token;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public PushTokenType getType() {
        return pushTokenId.getType();
    }
}
