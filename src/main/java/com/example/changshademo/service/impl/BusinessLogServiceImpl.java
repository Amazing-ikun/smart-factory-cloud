package com.example.changshademo.service.impl;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.BusinessLog;
import com.example.changshademo.mapper.BusinessLogMapper;
import com.example.changshademo.service.BusinessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessLogServiceImpl implements BusinessLogService {

    private final BusinessLogMapper businessLogMapper;

    @Override
    public PageResult<BusinessLog> findPage(int page, int pageSize) {
        List<BusinessLog> items = businessLogMapper.findPage((page - 1) * pageSize, pageSize);
        long total = businessLogMapper.count();
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    public void addLog(Integer userId, String userName, String operation, String module, String detail) {
        BusinessLog log = new BusinessLog();
        log.setUserId(userId);
        log.setUserName(userName);
        log.setOperation(operation);
        log.setModule(module);
        log.setDetail(detail);
        businessLogMapper.insert(log);
    }
}
