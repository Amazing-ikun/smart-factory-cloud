package com.example.changshademo.service;

import com.example.changshademo.entity.EquipmentProduct;

import java.util.List;

public interface EquipmentProductService {
    List<EquipmentProduct> findAll();
    EquipmentProduct findById(Integer id);
    List<EquipmentProduct> findByEquipmentId(Integer equipmentId);
    List<EquipmentProduct> findByProductId(Integer productId);
    List<EquipmentProduct> findByFactoryId(Integer factoryId);
    int insert(EquipmentProduct ep);
    void deleteById(Integer id, Integer updateUserid);
}
