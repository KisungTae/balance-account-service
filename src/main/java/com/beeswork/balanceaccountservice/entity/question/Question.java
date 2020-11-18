package com.beeswork.balanceaccountservice.entity.question;


import com.beeswork.balanceaccountservice.entity.account.AccountQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "question")
public class Question {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "top_option")
    private String topOption;

    @Column(name = "bottom_option")
    private String bottomOption;

    @OneToMany(mappedBy = "question",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<AccountQuestion> accountQuestions = new ArrayList<>();

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
