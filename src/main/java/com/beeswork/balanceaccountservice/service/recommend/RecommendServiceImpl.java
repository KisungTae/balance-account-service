package com.beeswork.balanceaccountservice.service.recommend;

import com.beeswork.balanceaccountservice.dao.account.AccountDAO;
import com.beeswork.balanceaccountservice.dao.question.QuestionDAO;
import com.beeswork.balanceaccountservice.entity.account.Account;
import com.beeswork.balanceaccountservice.exception.account.AccountNotFoundException;
import com.beeswork.balanceaccountservice.service.base.BaseServiceImpl;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
public class RecommendServiceImpl extends BaseServiceImpl implements RecommendService {

    private final AccountDAO accountDAO;
    private final QuestionDAO questionDAO;

    private final GeometryFactory geometryFactory;

    @Autowired
    public RecommendServiceImpl(ModelMapper modelMapper, AccountDAO accountDAO, QuestionDAO questionDAO, GeometryFactory geometryFactory) {
        super(modelMapper);
        this.accountDAO = accountDAO;
        this.questionDAO = questionDAO;
        this.geometryFactory = geometryFactory;
    }

    // TEST 1. matches are mapped by matcher_id not matched_id
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Account> accountsByLocation(UUID accountId, int distance, int minAge, int maxAge, boolean showMe, double latitude, double longitude) throws AccountNotFoundException {

        Account account = accountDAO.findById(accountId);
        Point location = geometryFactory.createPoint(new Coordinate(latitude, longitude));



        accountDAO.findAllWithin(accountId, distance, minAge, maxAge, showMe, account.getIndex(), location);





        return null;
    }



}
