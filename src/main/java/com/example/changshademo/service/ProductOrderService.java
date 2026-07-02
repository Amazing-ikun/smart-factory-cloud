package com.example.changshademo.service;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.ProductOrder;

import java.util.List;

/**
 * 订单业务逻辑接口
 */
public interface ProductOrderService {
    /**
     * 查询所有订单
     */
    List<ProductOrder> findAll();

    PageResult<ProductOrder> findPage(int page, int pageSize);

    /**
     * 根据ID查询订单
     */
    ProductOrder findById(Integer id);

    /**
     * 根据订单编号查询订单
     */
    ProductOrder findByOrderSeq(String orderSeq);

    /**
     * 根据工厂ID查询订单列表
     */
    List<ProductOrder> findByFactoryId(Integer factoryId);

    /**
     * 根据订单状态查询订单列表
     */
    List<ProductOrder> findByOrderStatus(Integer orderStatus);

    /**
     * 根据产品ID查询订单列表
     */
    List<ProductOrder> findByProductId(Integer productId);

    /**
     * 新增订单
     */
    int insert(ProductOrder productOrder);

    /**
     * 更新订单
     */
    int update(ProductOrder productOrder);

    /**
     * 更新订单状态
     */
    int updateStatus(Integer id, Integer orderStatus, Integer updateUserid);

    /**
     * 删除订单（逻辑删除）
     */
    int deleteById(Integer id, Integer updateUserid);
}
