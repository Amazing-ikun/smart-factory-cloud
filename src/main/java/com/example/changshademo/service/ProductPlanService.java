package com.example.changshademo.service;

import com.example.changshademo.entity.ProductPlan;

import java.time.LocalDate;
import java.util.List;

public interface ProductPlanService {
    List<ProductPlan> findAll();
    ProductPlan findById(Integer id);
    ProductPlan findByPlanSeq(String planSeq);
    List<ProductPlan> findByOrderId(Integer orderId);
    List<ProductPlan> findByProductId(Integer productId);
    List<ProductPlan> findByFactoryId(Integer factoryId);
    List<ProductPlan> findByStatus(Integer status);
    List<ProductPlan> findByDateRange(LocalDate start, LocalDate end);
    int insert(ProductPlan plan);
    int update(ProductPlan plan);
    void deleteById(Integer id, Integer updateUserid);
}
