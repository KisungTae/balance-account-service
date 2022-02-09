package com.beeswork.balanceaccountservice.dto.chat;

import com.beeswork.balanceaccountservice.constant.PushType;
import com.beeswork.balanceaccountservice.dto.common.Pushable;
import com.beeswork.balanceaccountservice.entity.pushtoken.PushToken;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDTO implements Pushable {

    private static final String PUSH_TITLE_CHAT_MESSAGE = "push.title.chat.message";
    private static final String PUSH_BODY_CHAT_MESSAGE  = "push.body.chat.message";

    private PushType pushType   = PushType.CHAT_MESSAGE;
    private UUID     id;
    private UUID     accountId;
    private UUID     recipientId;
    private String   body;
    private Long     chatId;
    private Date     createdAt;
    private String   senderName = "";

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
    public String[] getPushTitleArguments() {
        return null;
    }

    @Override
    public String[] getPushBodyArguments() {
        String[] args = new String[]{senderName};
        senderName = null;
        return args;
    }

    @Override
    public String getPushTitleId() {
        return null;
    }

    @Override
    public String getPushBodyId() {
        return null;
    }
}
