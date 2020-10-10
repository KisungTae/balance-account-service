package com.beeswork.balanceaccountservice.dto.firebase;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FirebaseNotificationDTO {

    private String       notificationType;
    private String       message;
    private List<String> tokens = new ArrayList<>();
}
