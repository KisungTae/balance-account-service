package com.beeswork.balanceaccountservice.entity.photo;


import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "photo")
public class Photo {

    @EmbeddedId
    private PhotoId photoId;

    @Column(name = "key", insertable = false, updatable = false)
    private String key;

    @Column(name = "sequence", unique = true, nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sequence;

    @Column(name = "account_id", insertable = false, updatable = false)
    private UUID accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    private Account account;
}
