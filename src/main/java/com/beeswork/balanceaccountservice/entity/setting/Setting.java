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
@Table(name = "setting")
public class Setting {

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

    public Setting(Account account) {
        this.account = account;
    }
}
