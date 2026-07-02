package com.example.changshademo.service.impl;

import com.example.changshademo.entity.ProductPlan;
import com.example.changshademo.mapper.ProductPlanMapper;
import com.example.changshademo.service.BaseService;
import com.example.changshademo.service.ProductPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPlanServiceImpl extends BaseService implements ProductPlanService {
    private final ProductPlanMapper productPlanMapper;

    @Override
    public List<ProductPlan> findAll() {
        logQuery("生产计划");
        return productPlanMapper.findAll();
    }

    @Override
    public ProductPlan findById(Integer id) {
        logQuery("生产计划", "ID=" + id);
        return productPlanMapper.findById(id);
    }

    @Override
    public ProductPlan findByPlanSeq(String planSeq) {
        logQuery("生产计划", "计划编号=" + planSeq);
        return productPlanMapper.findByPlanSeq(planSeq);
    }

    @Override
    public List<ProductPlan> findByOrderId(Integer orderId) {
        logQuery("生产计划", "订单ID=" + orderId);
        return productPlanMapper.findByOrderId(orderId);
    }

    @Override
    public List<ProductPlan> findByProductId(Integer productId) {
        logQuery("生产计划", "产品ID=" + productId);
        return productPlanMapper.findByProductId(productId);
    }

    @Override
    public List<ProductPlan> findByFactoryId(Integer factoryId) {
        logQuery("生产计划", "工厂ID=" + factoryId);
        return productPlanMapper.findByFactoryId(factoryId);
    }

    @Override
    public List<ProductPlan> findByStatus(Integer status) {
        logQuery("生产计划", "状态=" + status);
        return productPlanMapper.findByStatus(status);
    }

    @Override
    public List<ProductPlan> findByDateRange(LocalDate start, LocalDate end) {
        logQuery("生产计划", "时间范围=" + start + " 至 " + end);
        return productPlanMapper.findByDateRange(start, end);
    }

    @Override
    public int insert(ProductPlan plan) {
        logOperation("新增", "生产计划", "计划编号=" + plan.getPlanSeq());
        return productPlanMapper.insert(plan);
    }

    @Override
    public int update(ProductPlan plan) {
        logOperation("修改", "生产计划", "ID=" + plan.getId());
        return productPlanMapper.update(plan);
    }

    @Override
    public void deleteById(Integer id, Integer updateUserid) {
        logOperation("删除", "生产计划", "ID=" + id);
        productPlanMapper.deleteById(id, updateUserid);
    }
}
