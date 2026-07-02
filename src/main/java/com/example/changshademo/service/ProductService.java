package com.example.changshademo.service;

import com.example.changshademo.entity.Product;

import java.util.*;

/**
 * 产品业务逻辑接口
 */
public interface ProductService {
    /**
     * 查询所有产品
     */
    List<Product> findAll();

    /**
     * 根据ID查询产品
     */
    Product findById(Integer id);

    /**
     * 根据工厂ID查询产品列表
     */
    List<Product> findByFactoryId(Integer factoryId);

    /**
     * 根据产品名称模糊查询
     */
    List<Product> findByProductName(String productName);

    /**
     * 新增产品
     */
    int insert(Product product);

    /**
     * 更新产品
     */
    int update(Product product);

    /**
     * 删除产品（逻辑删除）
     */
    int deleteById(Integer id, Integer updateUserid);

    com.example.changshademo.common.PageResult<Product> findPage(int page, int pageSize);
}
