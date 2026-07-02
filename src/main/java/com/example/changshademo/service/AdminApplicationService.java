package com.example.changshademo.service;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.AdminApplication;

import java.util.List;

public interface AdminApplicationService {
    PageResult<AdminApplication> findPage(int page, int pageSize);
    List<AdminApplication> findByUserId(Integer userId);
    int add(AdminApplication application);
    void review(Integer id, int status, String replyContent, Integer replyUserId, String replyUserName);
    AdminApplication findById(Integer id);
}
