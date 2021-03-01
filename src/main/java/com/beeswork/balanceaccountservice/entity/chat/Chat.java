package com.beeswork.balanceaccountservice.entity.chat;

import com.beeswork.balanceaccountservice.entity.match.Match;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "chat")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Chat {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "chat",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();

}
