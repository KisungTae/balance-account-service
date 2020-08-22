package com.beeswork.balanceaccountservice.repository;

import com.beeswork.balanceaccountservice.entity.AccountType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTypeRepository extends CrudRepository<AccountType, Long> {
}
