package com.example.changshademo.service.impl;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.Feedback;
import com.example.changshademo.mapper.FeedbackMapper;
import com.example.changshademo.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;

    @Override
    public PageResult<Feedback> findPage(int page, int pageSize) {
        List<Feedback> items = feedbackMapper.findPage((page - 1) * pageSize, pageSize);
        long total = feedbackMapper.count();
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    public List<Feedback> findByUserId(Integer userId) {
        return feedbackMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    public int add(Feedback feedback) {
        return feedbackMapper.insert(feedback);
    }

    @Override
    public Feedback findById(Integer id) {
        return feedbackMapper.findById(id);
    }

    @Override
    @Transactional
    public void reply(Integer id, String replyContent, Integer replyUserId, String replyUserName) {
        Feedback fb = new Feedback();
        fb.setId(id);
        fb.setReplyContent(replyContent);
        fb.setReplyUserId(replyUserId);
        fb.setReplyUserName(replyUserName);
        feedbackMapper.updateReply(fb);
    }
}
