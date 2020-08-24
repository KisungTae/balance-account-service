package com.beeswork.balanceaccountservice.repository;

import com.beeswork.balanceaccountservice.entity.AccountType;
import com.beeswork.balanceaccountservice.repository.base.BaseRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface AccountTypeRepository extends BaseRepository<AccountType> {

    void test();
}
