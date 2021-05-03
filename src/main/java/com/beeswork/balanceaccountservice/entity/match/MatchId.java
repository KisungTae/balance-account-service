package com.beeswork.balanceaccountservice.entity.match;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;


@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MatchId implements Serializable {

    private UUID swiperId;
    private UUID swipedId;
}
