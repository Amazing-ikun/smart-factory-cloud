package com.example.changshademo.controller;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.DailyWork;
import com.example.changshademo.service.DailyWorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/daily-work")
@RequiredArgsConstructor
public class DailyWorkController {
    private final DailyWorkService dailyWorkService;

    @GetMapping("/list")
    public Result<PageResult<DailyWork>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(dailyWorkService.findPage(page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<DailyWork> getById(@PathVariable Integer id) {
        DailyWork work = dailyWorkService.findById(id);
        if (work != null) {
            return Result.success(work);
        }
        return Result.error("日报记录不存在");
    }

    @GetMapping("/schedule/{scheduleId}")
    public Result<List<DailyWork>> getByScheduleId(@PathVariable Integer scheduleId) {
        return Result.success(dailyWorkService.findByScheduleId(scheduleId));
    }

    @GetMapping("/equipment/{equipmentId}")
    public Result<List<DailyWork>> getByEquipmentId(@PathVariable Integer equipmentId) {
        return Result.success(dailyWorkService.findByEquipmentId(equipmentId));
    }

    @GetMapping("/factory/{factoryId}")
    public Result<List<DailyWork>> getByFactoryId(@PathVariable Integer factoryId) {
        return Result.success(dailyWorkService.findByFactoryId(factoryId));
    }

    @GetMapping("/complete-flag/{completeFlag}")
    public Result<List<DailyWork>> getByCompleteFlag(@PathVariable Integer completeFlag) {
        return Result.success(dailyWorkService.findByCompleteFlag(completeFlag));
    }

    @GetMapping("/date-range")
    public Result<List<DailyWork>> getByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        return Result.success(dailyWorkService.findByDateRange(start, end));
    }
}
