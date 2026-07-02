package com.example.changshademo.service.impl;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.DailyWork;
import com.example.changshademo.mapper.DailyWorkMapper;
import com.example.changshademo.service.BaseService;
import com.example.changshademo.service.DailyWorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyWorkServiceImpl extends BaseService implements DailyWorkService {
    private final DailyWorkMapper dailyWorkMapper;

    @Override
    public List<DailyWork> findAll() {
        logQuery("日报工");
        return dailyWorkMapper.findAll();
    }

    @Override
    public PageResult<DailyWork> findPage(int page, int pageSize) {
        List<DailyWork> items = dailyWorkMapper.findPage((page - 1) * pageSize, pageSize);
        long total = dailyWorkMapper.count();
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    public DailyWork findById(Integer id) {
        logQuery("日报工", "ID=" + id);
        return dailyWorkMapper.findById(id);
    }

    @Override
    public List<DailyWork> findByScheduleId(Integer scheduleId) {
        logQuery("日报工", "调度ID=" + scheduleId);
        return dailyWorkMapper.findByScheduleId(scheduleId);
    }

    @Override
    public List<DailyWork> findByEquipmentId(Integer equipmentId) {
        logQuery("日报工", "设备ID=" + equipmentId);
        return dailyWorkMapper.findByEquipmentId(equipmentId);
    }

    @Override
    public List<DailyWork> findByFactoryId(Integer factoryId) {
        logQuery("日报工", "工厂ID=" + factoryId);
        return dailyWorkMapper.findByFactoryId(factoryId);
    }

    @Override
    public List<DailyWork> findByCompleteFlag(Integer completeFlag) {
        logQuery("日报工", "完成标识=" + completeFlag);
        return dailyWorkMapper.findByCompleteFlag(completeFlag);
    }

    @Override
    public List<DailyWork> findByDateRange(LocalDateTime start, LocalDateTime end) {
        logQuery("日报工", "时间范围=" + start + " 至 " + end);
        return dailyWorkMapper.findByDateRange(start, end);
    }
}
