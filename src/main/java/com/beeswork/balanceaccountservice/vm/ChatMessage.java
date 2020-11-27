package com.beeswork.balanceaccountservice.vm;


import com.beeswork.balanceaccountservice.validator.ValidUUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {

    private Long chatId;

    @ValidUUID
    private String from;

    private String text;
    private String recipient;
    private String time;


    public ChatMessage(String from, String text, String recipient) {
        this.from = from;
        this.text = text;
        this.recipient = recipient;
        this.time = new Date().toString();
    }
}
