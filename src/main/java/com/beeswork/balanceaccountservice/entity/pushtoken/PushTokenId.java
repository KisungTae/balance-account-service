package com.beeswork.balanceaccountservice.entity.pushtoken;


import com.beeswork.balanceaccountservice.constant.PushTokenType;
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
public class PushTokenId implements Serializable {

    private UUID accountId;

    @Enumerated
    private PushTokenType type;
}
