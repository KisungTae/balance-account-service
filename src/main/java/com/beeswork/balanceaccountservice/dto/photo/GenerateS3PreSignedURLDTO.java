package com.beeswork.balanceaccountservice.dto.photo;


import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
public class GenerateS3PreSignedURLDTO {

    private URL s3PreSignedURL;
    private String key;
}
