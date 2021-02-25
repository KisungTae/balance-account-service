package com.beeswork.balanceaccountservice.entity.account;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "push_notification")
public class PushNotification {

    @EmbeddedId
    private PushNotificationId pushNotificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Column(name = "token")
    private String token;
}
