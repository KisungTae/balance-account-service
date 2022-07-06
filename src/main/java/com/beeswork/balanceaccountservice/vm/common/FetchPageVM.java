package com.beeswork.balanceaccountservice.vm.common;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class FetchPageVM {

    private Long loadKey;

    @NotNull
    private Integer loadSize;

    private boolean append;
    private boolean includeLoadKey;
}
