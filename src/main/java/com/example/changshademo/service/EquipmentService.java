package com.example.changshademo.service;

import com.example.changshademo.entity.Equipment;

import java.util.List;

public interface EquipmentService {
    List<Equipment> findAll();
    Equipment findById(Integer id);
    List<Equipment> findByFactoryId(Integer factoryId);
    List<Equipment> findByStatus(Integer status);
    int insert(Equipment equipment);
    int update(Equipment equipment);
    int updateImage(Integer id, String equipmentImgUrl, Integer updateUserid);
    int deleteById(Integer id, Integer updateUserid);
    com.example.changshademo.common.PageResult<Equipment> findPage(int page, int pageSize);
}
