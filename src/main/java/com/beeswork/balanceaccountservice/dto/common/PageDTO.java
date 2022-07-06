package com.beeswork.balanceaccountservice.dto.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PageDTO<T> {

    private List<T> items = new ArrayList<>();
    private long swipeCount;
    private Date swipeCountCountedAt;
    private long matchCount;
    private Date matchCountCountedAt;
    private long unreadChatCount;
    private Date unreadChatCountCountedAt;

}
