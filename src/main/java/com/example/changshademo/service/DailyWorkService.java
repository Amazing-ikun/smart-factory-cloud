package com.example.changshademo.service;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.DailyWork;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyWorkService {
    List<DailyWork> findAll();
    PageResult<DailyWork> findPage(int page, int pageSize);
    DailyWork findById(Integer id);
    List<DailyWork> findByScheduleId(Integer scheduleId);
    List<DailyWork> findByEquipmentId(Integer equipmentId);
    List<DailyWork> findByFactoryId(Integer factoryId);
    List<DailyWork> findByCompleteFlag(Integer completeFlag);
    List<DailyWork> findByDateRange(LocalDateTime start, LocalDateTime end);
}
