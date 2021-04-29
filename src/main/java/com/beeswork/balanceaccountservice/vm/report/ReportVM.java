package com.beeswork.balanceaccountservice.vm.report;

import com.beeswork.balanceaccountservice.validator.ValidUUID;
import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class ReportVM extends AccountIdentityVM {

    @ValidUUID
    private UUID reportedId;

    @NotNull
    private Integer reportReasonId;

    private String description;
}
