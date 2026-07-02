package com.example.changshademo.service;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.Message;

public interface MessageService {
    PageResult<Message> findByUserId(Integer userId, int page, int pageSize);
    long countUnread(Integer userId);
    int send(Message message);
    void markAsRead(Integer id);
    Message findById(Integer id);
}
