package com.example.changshademo.service.impl;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.AdminApplication;
import com.example.changshademo.mapper.AdminApplicationMapper;
import com.example.changshademo.service.AdminApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminApplicationServiceImpl implements AdminApplicationService {

    private final AdminApplicationMapper adminApplicationMapper;

    @Override
    public PageResult<AdminApplication> findPage(int page, int pageSize) {
        List<AdminApplication> items = adminApplicationMapper.findPage((page - 1) * pageSize, pageSize);
        long total = adminApplicationMapper.count();
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    public List<AdminApplication> findByUserId(Integer userId) {
        return adminApplicationMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    public int add(AdminApplication application) {
        return adminApplicationMapper.insert(application);
    }

    @Override
    public AdminApplication findById(Integer id) {
        return adminApplicationMapper.findById(id);
    }

    @Override
    @Transactional
    public void review(Integer id, int status, String replyContent, Integer replyUserId, String replyUserName) {
        AdminApplication app = new AdminApplication();
        app.setId(id);
        app.setStatus(status);
        app.setReplyContent(replyContent);
        app.setReplyUserId(replyUserId);
        app.setReplyUserName(replyUserName);
        adminApplicationMapper.updateReply(app);
    }
}
