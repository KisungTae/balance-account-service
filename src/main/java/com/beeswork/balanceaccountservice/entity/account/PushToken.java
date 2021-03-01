package com.beeswork.balanceaccountservice.entity.account;


import com.beeswork.balanceaccountservice.constant.PushTokenType;
import lombok.Data;
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

    @Column(name = "key")
    private String key;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public PushToken(Account account, PushTokenType type, String key, Date createdAt) {
        this.pushTokenId = new PushTokenId(account.getId(), type);
        this.account = account;
        this.key = key;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }
}
