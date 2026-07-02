package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.ProductSchedule;
import com.example.changshademo.enums.ScheduleStatusEnum;
import com.example.changshademo.service.ProductScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ProductScheduleController {
    private final ProductScheduleService productScheduleService;

    @GetMapping("/list")
    public Result<List<ProductSchedule>> list() {
        return Result.success(productScheduleService.findAll());
    }

    @GetMapping("/{id}")
    public Result<ProductSchedule> getById(@PathVariable Integer id) {
        ProductSchedule schedule = productScheduleService.findById(id);
        if (schedule != null) {
            return Result.success(schedule);
        }
        return Result.error("调度不存在");
    }

    @GetMapping("/seq/{scheduleSeq}")
    public Result<ProductSchedule> getByScheduleSeq(@PathVariable String scheduleSeq) {
        ProductSchedule schedule = productScheduleService.findByScheduleSeq(scheduleSeq);
        if (schedule != null) {
            return Result.success(schedule);
        }
        return Result.error("调度不存在");
    }

    @GetMapping("/plan/{planId}")
    public Result<List<ProductSchedule>> getByPlanId(@PathVariable Integer planId) {
        return Result.success(productScheduleService.findByPlanId(planId));
    }

    @GetMapping("/product/{productId}")
    public Result<List<ProductSchedule>> getByProductId(@PathVariable Integer productId) {
        return Result.success(productScheduleService.findByProductId(productId));
    }

    @GetMapping("/equipment/{equipmentId}")
    public Result<List<ProductSchedule>> getByEquipmentId(@PathVariable Integer equipmentId) {
        return Result.success(productScheduleService.findByEquipmentId(equipmentId));
    }

    @GetMapping("/factory/{factoryId}")
    public Result<List<ProductSchedule>> getByFactoryId(@PathVariable Integer factoryId) {
        return Result.success(productScheduleService.findByFactoryId(factoryId));
    }

    @GetMapping("/status/{status}")
    public Result<List<ProductSchedule>> getByScheduleStatus(@PathVariable Integer status) {
        return Result.success(productScheduleService.findByScheduleStatus(status));
    }

    @GetMapping("/date-range")
    public Result<List<ProductSchedule>> getByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return Result.success(productScheduleService.findByDateRange(start, end));
    }

    @GetMapping("/status-enums")
    public Result<List<ScheduleStatusEnum>> getStatusEnums() {
        return Result.success(List.of(ScheduleStatusEnum.values()));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody ProductSchedule schedule) {
        schedule.setCreateUserid(CurrentUserUtils.getCurrentUserId());
        productScheduleService.insert(schedule);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @Valid @RequestBody ProductSchedule schedule) {
        schedule.setId(id);
        schedule.setUpdateUserid(CurrentUserUtils.getCurrentUserId());
        productScheduleService.update(schedule);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        productScheduleService.deleteById(id, CurrentUserUtils.getCurrentUserId());
        return Result.success("删除成功");
    }
}
