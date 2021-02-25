package com.beeswork.balanceaccountservice.entity.account;


import com.beeswork.balanceaccountservice.constant.PushNotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationId implements Serializable {

    private UUID accountId;

    @Enumerated
    private PushNotificationType type;
}
