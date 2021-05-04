package com.beeswork.balanceaccountservice.entity.account;


import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.google.firebase.database.DatabaseError;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "wallet")
public class Wallet {

    @Id
    private long accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @MapsId
    private Account account;

    @Column(name = "point")
    private int point;

    @Column(name = "free_swipe")
    private int freeSwipe;

    @Column(name = "free_swipe_recharged_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date freeSwipeRechargedAt;
}
