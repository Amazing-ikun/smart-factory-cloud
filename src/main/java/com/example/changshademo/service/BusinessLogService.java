package com.example.changshademo.service;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.BusinessLog;

public interface BusinessLogService {
    PageResult<BusinessLog> findPage(int page, int pageSize);
    void addLog(Integer userId, String userName, String operation, String module, String detail);
}
