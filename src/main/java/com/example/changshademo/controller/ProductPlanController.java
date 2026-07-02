package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.ProductPlan;
import com.example.changshademo.enums.PlanStatusEnum;
import com.example.changshademo.service.ProductPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class ProductPlanController {
    private final ProductPlanService productPlanService;

    @GetMapping("/list")
    public Result<List<ProductPlan>> list() {
        return Result.success(productPlanService.findAll());
    }

    @GetMapping("/{id}")
    public Result<ProductPlan> getById(@PathVariable Integer id) {
        ProductPlan plan = productPlanService.findById(id);
        if (plan != null) {
            return Result.success(plan);
        }
        return Result.error("计划不存在");
    }

    @GetMapping("/seq/{planSeq}")
    public Result<ProductPlan> getByPlanSeq(@PathVariable String planSeq) {
        ProductPlan plan = productPlanService.findByPlanSeq(planSeq);
        if (plan != null) {
            return Result.success(plan);
        }
        return Result.error("计划不存在");
    }

    @GetMapping("/order/{orderId}")
    public Result<List<ProductPlan>> getByOrderId(@PathVariable Integer orderId) {
        return Result.success(productPlanService.findByOrderId(orderId));
    }

    @GetMapping("/product/{productId}")
    public Result<List<ProductPlan>> getByProductId(@PathVariable Integer productId) {
        return Result.success(productPlanService.findByProductId(productId));
    }

    @GetMapping("/factory/{factoryId}")
    public Result<List<ProductPlan>> getByFactoryId(@PathVariable Integer factoryId) {
        return Result.success(productPlanService.findByFactoryId(factoryId));
    }

    @GetMapping("/status/{status}")
    public Result<List<ProductPlan>> getByStatus(@PathVariable Integer status) {
        return Result.success(productPlanService.findByStatus(status));
    }

    @GetMapping("/date-range")
    public Result<List<ProductPlan>> getByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return Result.success(productPlanService.findByDateRange(start, end));
    }

    @GetMapping("/status-enums")
    public Result<List<PlanStatusEnum>> getStatusEnums() {
        return Result.success(List.of(PlanStatusEnum.values()));
    }

    @PostMapping
    public Result<Void> add(@RequestBody ProductPlan plan) {
        plan.setCreateUserid(CurrentUserUtils.getCurrentUserId());
        productPlanService.insert(plan);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody ProductPlan plan) {
        plan.setUpdateUserid(CurrentUserUtils.getCurrentUserId());
        productPlanService.update(plan);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        productPlanService.deleteById(id, CurrentUserUtils.getCurrentUserId());
        return Result.success("删除成功");
    }
}
