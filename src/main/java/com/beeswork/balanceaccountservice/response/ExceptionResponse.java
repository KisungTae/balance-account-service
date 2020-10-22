package com.beeswork.balanceaccountservice.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    private int status;
    private String error;
    private String message;
    private Map<String, String> fieldErrorMessages = new HashMap<>();
}
