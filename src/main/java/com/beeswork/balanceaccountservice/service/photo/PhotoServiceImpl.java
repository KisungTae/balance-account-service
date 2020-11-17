package com.beeswork.balanceaccountservice.service.photo;

import com.amazonaws.services.s3.AmazonS3;
import com.beeswork.balanceaccountservice.dto.photo.GenerateS3PreSignedURLDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class PhotoServiceImpl implements PhotoService {


    private final AmazonS3 amazonS3;


    @Autowired
    public PhotoServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }



}
