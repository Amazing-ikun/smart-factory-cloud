package com.example.changshademo.service;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.OrderTrack;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderTrackService {
    List<OrderTrack> findAll();
    OrderTrack findById(Integer id);
    List<OrderTrack> findByScheduleId(Integer scheduleId);
    List<OrderTrack> findByPlanId(Integer planId);
    List<OrderTrack> findByProductId(Integer productId);
    List<OrderTrack> findByFactoryId(Integer factoryId);

    PageResult<OrderTrack> findPage(Integer page, Integer pageSize,
                                    Integer factoryId,
                                    LocalDateTime start, LocalDateTime end);
}
