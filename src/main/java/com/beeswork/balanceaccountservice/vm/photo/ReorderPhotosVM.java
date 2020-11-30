package com.beeswork.balanceaccountservice.vm.photo;

import com.beeswork.balanceaccountservice.vm.account.AccountIdentityVM;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Map;

@Getter
@Setter
public class ReorderPhotosVM extends AccountIdentityVM {

    @Size(min = 2, max = 6)
    private Map<String, Long> photoOrders;
}
