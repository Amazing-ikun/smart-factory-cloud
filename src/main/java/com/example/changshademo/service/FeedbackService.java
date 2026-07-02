package com.example.changshademo.service;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.Feedback;

import java.util.List;

public interface FeedbackService {
    PageResult<Feedback> findPage(int page, int pageSize);
    List<Feedback> findByUserId(Integer userId);
    int add(Feedback feedback);
    void reply(Integer id, String replyContent, Integer replyUserId, String replyUserName);
    Feedback findById(Integer id);
}
