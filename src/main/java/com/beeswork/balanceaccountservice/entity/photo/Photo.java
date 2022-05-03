package com.beeswork.balanceaccountservice.entity.photo;


import com.beeswork.balanceaccountservice.constant.Delimiter;
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

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Photo(Account account, String key, int sequence, Date createdAt) {
        this.photoId = new PhotoId(account.getId(), key);
        this.sequence = sequence;
        this.account = account;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public String getKey() {
        return photoId.getKey();
    }

    public UUID getAccountId() {
        return photoId.getAccountId();
    }

    @Override
    public int compareTo(@NonNull Photo o) {
        return Integer.compare(this.sequence, o.sequence);
    }

    public static String getPhotoKey(UUID accountId, String key) {
        return accountId.toString() + Delimiter.FORWARD_SLASH + key;
    }

}
