package com.beeswork.balanceaccountservice.vm.photo;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Size;
import java.util.Map;

@Getter
@Setter
public class ReorderPhotosVM {

    @Size(min = 2, max = 6)
    private Map<String, Integer> photoOrders;
}
