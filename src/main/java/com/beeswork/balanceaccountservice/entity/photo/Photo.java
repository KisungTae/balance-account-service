package com.beeswork.balanceaccountservice.entity.photo;


import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.entity.profile.Profile;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    private Account account;

    @Column(name = "sequence")
    private int sequence;

    public Photo(Account account, String key, int sequence) {
        this.photoId = new PhotoId(account.getId(), key);
        this.sequence = sequence;
        this.account = account;
    }

    public String getKey() {
        return photoId.getKey();
    }

    @Override
    public int compareTo(@NonNull Photo o) {
        return Integer.compare(this.sequence, o.sequence);
    }
}
