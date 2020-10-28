package com.beeswork.balanceaccountservice.config;


import com.beeswork.balanceaccountservice.config.properties.FirebaseProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Configuration
public class FirebaseConfig {

    private final FirebaseProperties firebaseProperties;

    @Autowired
    public FirebaseConfig(FirebaseProperties firebaseProperties) {
        this.firebaseProperties = firebaseProperties;
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {

        FileInputStream serviceAccount = new FileInputStream(firebaseProperties.getServiceAccountKeyPath());
        FirebaseOptions options = FirebaseOptions.builder()
                                                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                                 .setDatabaseUrl(firebaseProperties.getDatabaseURL())
                                                 .build();

        if (FirebaseApp.getApps().isEmpty()) FirebaseApp.initializeApp(options);
        return FirebaseMessaging.getInstance();
    }
}
