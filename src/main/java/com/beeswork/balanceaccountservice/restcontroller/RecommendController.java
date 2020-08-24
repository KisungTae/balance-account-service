package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.config.properties.AWSProperties;
import com.beeswork.balanceaccountservice.entity.AccountType;
import com.beeswork.balanceaccountservice.entity.QAccountType;
import com.beeswork.balanceaccountservice.repository.AccountTypeRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/recommend")
public class RecommendController {


    @Autowired
    private AWSProperties awsProperties;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/test")
    public String test() {
        System.out.println("한국");
        System.out.println(awsProperties.toString());


        AccountType accountType = new AccountType();
        accountType.setDescription("페이스북 로그인");


        accountTypeRepository.test();

        List<AccountType> accountTypes = new JPAQueryFactory(entityManager).selectFrom(QAccountType.accountType)
                                                                           .where(QAccountType.accountType.description.like("%페이스%"))
                                                                           .fetch();





        return messageSource.getMessage("test.notfound", null, Locale.getDefault());
    }
}
