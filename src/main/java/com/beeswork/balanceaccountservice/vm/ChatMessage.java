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

    private String from;
    private String text;
    private String to;
    private String time;


    public ChatMessage(String from, String text, String to) {
        this.from = from;
        this.text = text;
        this.to = to;
        this.time = new Date().toString();
    }
}
