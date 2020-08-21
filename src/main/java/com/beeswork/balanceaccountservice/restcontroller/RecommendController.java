package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.config.properties.AWSProperties;
import com.beeswork.balanceaccountservice.config.properties.ConnectionPoolProperties;
import com.beeswork.balanceaccountservice.config.properties.HibernateProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private ConnectionPoolProperties connectionPoolProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

    @Autowired
    private AWSProperties awsProperties;

    @GetMapping("/test")
    public String test() {
        System.out.println("dddddddddddddddddddddddddddddddddddddddd");
        System.out.println(connectionPoolProperties.toString());
        System.out.println(hibernateProperties.toString());
        System.out.println(awsProperties.toString());
        return "test";
    }
}
