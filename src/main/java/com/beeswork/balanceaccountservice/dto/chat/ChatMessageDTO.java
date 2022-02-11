package com.beeswork.balanceaccountservice.dto.chat;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDTO implements Pushable {

    @JsonIgnore
    private static final String PUSH_TITLE_CHAT_MESSAGE = "push.title.chat.message";

    @JsonIgnore
    private static final String PUSH_BODY_CHAT_MESSAGE  = "push.body.chat.message";

    private UUID     id;
    private UUID     accountId;
    private UUID     recipientId;
    private String   body;
    private Long     chatId;
    private Date     createdAt;
    private String   senderName;

    @QueryProjection
    public ChatMessageDTO(UUID id, Date createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    @QueryProjection
    public ChatMessageDTO(UUID id, String body, Long chatId, Date createdAt) {
        this.id = id;
        this.body = body;
        this.chatId = chatId;
        this.createdAt = createdAt;
    }

    @Override
    @JsonIgnore
    public PushType getPushType() {
        return PushType.CHAT_MESSAGE;
    }

    @Override
    @JsonIgnore
    public String[] getPushTitleArguments() {
        return null;
    }

    @Override
    @JsonIgnore
    public String[] getPushBodyArguments() {
        if (StringUtils.isBlank(senderName)) {
            this.senderName = "";
        }
        String[] args = new String[] {senderName};
        this.senderName = null;
        return args;
    }

    @Override
    @JsonIgnore
    public String getPushTitleId() {
        return null;
    }

    @Override
    @JsonIgnore
    public String getPushBodyId() {
        return null;
    }
}
