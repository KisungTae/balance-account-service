package com.beeswork.balanceaccountservice.entity.setting;

import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "push_setting")
public class PushSetting {

    @Id
    private UUID accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @MapsId
    private Account account;

    @Column(name = "match_push")
    private boolean matchPush;

    @Column(name = "clicked_push")
    private boolean clickedPush;

    @Column(name = "chat_message_push")
    private boolean chatMessagePush;

    @Column(name = "email_push")
    private boolean emailPush;

    public PushSetting(Account account) {
        this.account = account;
        this.matchPush = true;
        this.clickedPush = true;
        this.chatMessagePush = true;
    }
}
