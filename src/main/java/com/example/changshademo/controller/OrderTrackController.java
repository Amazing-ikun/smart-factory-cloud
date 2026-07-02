package com.example.changshademo.controller;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.OrderTrack;
import com.example.changshademo.service.OrderTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/order-track")
@RequiredArgsConstructor
public class OrderTrackController {
    private final OrderTrackService orderTrackService;

    @GetMapping("/list")
    public Result<PageResult<OrderTrack>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Integer factoryId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        PageResult<OrderTrack> result = orderTrackService.findPage(page, pageSize, factoryId, start, end);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<OrderTrack> getById(@PathVariable Integer id) {
        OrderTrack track = orderTrackService.findById(id);
        if (track != null) {
            return Result.success(track);
        }
        return Result.error("订单跟踪记录不存在");
    }
}
