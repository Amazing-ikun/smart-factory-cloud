package com.example.changshademo.service.impl;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.OrderTrack;
import com.example.changshademo.mapper.OrderTrackMapper;
import com.example.changshademo.service.BaseService;
import com.example.changshademo.service.OrderTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderTrackServiceImpl extends BaseService implements OrderTrackService {
    private final OrderTrackMapper orderTrackMapper;

    @Override
    public List<OrderTrack> findAll() {
        logQuery("订单跟踪");
        return orderTrackMapper.findAll();
    }

    @Override
    public OrderTrack findById(Integer id) {
        logQuery("订单跟踪", "ID=" + id);
        return orderTrackMapper.findById(id);
    }

    @Override
    public List<OrderTrack> findByScheduleId(Integer scheduleId) {
        logQuery("订单跟踪", "调度ID=" + scheduleId);
        return orderTrackMapper.findByScheduleId(scheduleId);
    }

    @Override
    public List<OrderTrack> findByPlanId(Integer planId) {
        logQuery("订单跟踪", "计划ID=" + planId);
        return orderTrackMapper.findByPlanId(planId);
    }

    @Override
    public List<OrderTrack> findByProductId(Integer productId) {
        logQuery("订单跟踪", "产品ID=" + productId);
        return orderTrackMapper.findByProductId(productId);
    }

    @Override
    public List<OrderTrack> findByFactoryId(Integer factoryId) {
        logQuery("订单跟踪", "工厂ID=" + factoryId);
        return orderTrackMapper.findByFactoryId(factoryId);
    }

    @Override
    public PageResult<OrderTrack> findPage(Integer page, Integer pageSize,
                                           Integer factoryId,
                                           LocalDateTime start, LocalDateTime end) {
        Map<String, Object> params = new HashMap<>();
        params.put("factoryId", factoryId);
        params.put("start", start);
        params.put("end", end);
        params.put("offset", (page - 1) * pageSize);
        params.put("limit", pageSize);

        List<OrderTrack> items = orderTrackMapper.findPage(params);
        long total = orderTrackMapper.count(params);
        return new PageResult<>(items, total, page, pageSize);
    }
}
