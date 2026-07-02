package com.example.changshademo.service.impl;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.Message;
import com.example.changshademo.mapper.MessageMapper;
import com.example.changshademo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public PageResult<Message> findByUserId(Integer userId, int page, int pageSize) {
        List<Message> items = messageMapper.findByUserId(userId, (page - 1) * pageSize, pageSize);
        long total = messageMapper.countByUserId(userId);
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    public long countUnread(Integer userId) {
        return messageMapper.countUnread(userId);
    }

    @Override
    @Transactional
    public int send(Message message) {
        return messageMapper.insert(message);
    }

    @Override
    @Transactional
    public void markAsRead(Integer id) {
        messageMapper.markAsRead(id);
    }

    @Override
    public Message findById(Integer id) {
        return messageMapper.findById(id);
    }
}
