package com.beeswork.balanceaccountservice.restcontroller;

import com.beeswork.balanceaccountservice.config.properties.AWSProperties;
import com.beeswork.balanceaccountservice.entity.*;
import com.beeswork.balanceaccountservice.repository.AccountTypeRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.tomcat.jni.Local;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import javax.transaction.Transactional;
import java.sql.Struct;
import java.util.Date;
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
    @Transactional
    public String test() {
        System.out.println("한국");
        System.out.println(awsProperties.toString());


        AccountType accountType = new AccountType();
        accountType.setDescription("페이스북 로그인");
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(126.807883, 37.463557));

        entityManager.persist(accountType);
        entityManager.flush();

        Account account = new Account(null,
                                      false,
                                      "test1",
                                      "test@gmail.com",
                                      1987,
                                      false,
                                      "this is about",
                                      19,
                                      10,
                                      10,
                                      10,
                                      new Date(),
                                      point,
                                      accountType,
                                      new Date(),
                                      new Date());

        Question question = new Question(null, "this is question", "top option", "bottom option",new Date() ,new Date());


        entityManager.persist(account);
        entityManager.persist(question);
        entityManager.flush();

        AccountQuestionRel accountQuestionRel = new AccountQuestionRel();


        accountQuestionRel.setAccountQuestionRelId(new AccountQuestionRelId(account.getId(), question.getId()));
        accountQuestionRel.setEnabled(true);
        accountQuestionRel.setSelected(true);
        accountQuestionRel.setCreated_at(new Date());
        accountQuestionRel.setUpdated_at(new Date());

        entityManager.persist(accountQuestionRel);

        

        List<AccountType> accountTypes = new JPAQueryFactory(entityManager).selectFrom(QAccountType.accountType)
                .where(QAccountType.accountType.description.like("%페이스%"))
                .fetch();


        return messageSource.getMessage("test.notfound", null, Locale.getDefault());
    }
}
