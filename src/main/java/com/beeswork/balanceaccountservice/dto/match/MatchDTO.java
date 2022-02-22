package com.beeswork.balanceaccountservice.dto.match;


import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.entity.chat.ChatMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MatchDTO implements Pushable {

    @JsonIgnore
    private static final String PUSH_TITLE_MATCH = "push.title.match";

    @JsonIgnore
    private static final String PUSH_BODY_MATCH = "push.body.match";

    private long    id;
    private UUID    chatId;
    private UUID    swiperId;
    private UUID    swipedId;
    private Boolean unmatched;
    private long    lastReadChatMessageId;
    private long    lastChatMessageId;
    private String  lastChatMessageBody;
    private Date    createdAt;
    private String  swipedName;
    private String  swipedProfilePhotoKey;
    private Boolean swipedDeleted;


    @QueryProjection
    public MatchDTO(long id,
                    UUID chatId,
                    UUID swiperId,
                    UUID swipedId,
                    Boolean unmatched,
                    long lastReadChatMessageId,
                    long lastChatMessageId,
                    String lastChatMessageBody,
                    String swipedName,
                    String swipedProfilePhotoKey,
                    Boolean swipedDeleted) {
        this.id = id;
        this.chatId = chatId;
        this.swiperId = swiperId;
        this.swipedId = swipedId;
        this.unmatched = unmatched;
        this.lastReadChatMessageId = lastReadChatMessageId;
        this.lastChatMessageId = lastChatMessageId;
        this.lastChatMessageBody = lastChatMessageBody;
        this.swipedName = swipedName;
        this.swipedProfilePhotoKey = swipedProfilePhotoKey;
        this.swipedDeleted = swipedDeleted;
    }

    @Override
    @JsonIgnore
    public PushType getPushType() {
        return PushType.MATCH;
    }

    @Override
    @JsonIgnore
    public UUID getRecipientId() {
        return swiperId;
    }

    @Override
    @JsonIgnore
    public String[] getPushTitleArguments() {
        return null;
    }

    @Override
    @JsonIgnore
    public String[] getPushBodyArguments() {
        return new String[]{swipedName};
    }

    @Override
    @JsonIgnore
    public String getPushTitleId() {
        return PUSH_TITLE_MATCH;
    }

    @Override
    @JsonIgnore
    public String getPushBodyId() {
        return PUSH_BODY_MATCH;
    }
}
