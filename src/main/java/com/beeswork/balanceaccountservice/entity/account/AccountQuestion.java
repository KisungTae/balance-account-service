package com.beeswork.balanceaccountservice.entity.account;


import com.beeswork.balanceaccountservice.entity.question.Question;
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
@Table(name = "account_question")
public class AccountQuestion {

    @EmbeddedId
    private AccountQuestionId accountQuestionId;

    @Column(name = "selected")
    private boolean selected;

    @Column(name = "answer")
    private boolean answer;

    @Column(name = "sequence")
    private int sequence;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    private Account account;

    @Column(name = "account_id",
            insertable = false,
            updatable = false)
    private UUID accountId;

    @Column(name = "question_id",
            insertable = false,
            updatable = false)
    private Integer questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("questionId")
    private Question question;

    public AccountQuestion(boolean selected, boolean answer, int sequence, Date createdAt, Date updatedAt, Account account, Question question) {
        this.accountQuestionId = new AccountQuestionId(account.getId(), question.getId());
        this.selected = selected;
        this.answer = answer;
        this.sequence = sequence;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.account = account;
        this.question = question;
    }

    public UUID getAccountId() {
        return accountQuestionId.getAccountId();
    }

    public Integer getQuestionId() {
        return accountQuestionId.getQuestionId();
    }
}
