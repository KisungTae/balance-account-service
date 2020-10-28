package com.beeswork.balanceaccountservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.FileInputStream;

@WebListener
public class BalanceServletContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("ServletContextListener destroyed");
    }

    //Run this before web application is started
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        try {

            FileInputStream serviceAccount = new FileInputStream("C:\\Users\\mtae\\Desktop\\Work\\Etc\\balance-896d6-firebase-adminsdk-sppjt-b4e2ac049d.json");

            FirebaseOptions options = FirebaseOptions.builder()
                                                     .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                                     .setDatabaseUrl("https://balance-896d6.firebaseio.com")
                                                     .build();
            FirebaseApp.initializeApp(options);

        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
        System.out.println("ServletContextListener started");
    }
}
