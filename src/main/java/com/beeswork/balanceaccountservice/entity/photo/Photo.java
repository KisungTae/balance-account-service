package com.beeswork.balanceaccountservice.entity.photo;


import com.beeswork.balanceaccountservice.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "photo")
public class Photo implements Comparable<Photo> {

    @EmbeddedId
    private PhotoId photoId;

    @Column(name = "key", insertable = false, updatable = false)
    private String key;

    @Column(name = "account_id", insertable = false, updatable = false)
    private UUID accountId;

    @Column(name = "sequence")
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    private Account account;

    @Override
    public int compareTo(@NonNull Photo o) {
        if (sequence == null || o.sequence == null) return 0;
        return this.sequence.compareTo(o.sequence);
    }
}
