package com.example.changshademo.service.impl;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.Equipment;
import com.example.changshademo.mapper.EquipmentMapper;
import com.example.changshademo.service.BaseService;
import com.example.changshademo.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl extends BaseService implements EquipmentService {
    private final EquipmentMapper equipmentMapper;

    @Override
    public List<Equipment> findAll() {
        logQuery("设备");
        return equipmentMapper.findAll();
    }

    @Override
    public Equipment findById(Integer id) {
        logQuery("设备", "ID=" + id);
        return equipmentMapper.findById(id);
    }

    @Override
    public List<Equipment> findByFactoryId(Integer factoryId) {
        logQuery("设备", "工厂ID=" + factoryId);
        return equipmentMapper.findByFactoryId(factoryId);
    }

    @Override
    public List<Equipment> findByStatus(Integer status) {
        logQuery("设备", "状态=" + status);
        return equipmentMapper.findByStatus(status);
    }

    @Override
    public int insert(Equipment equipment) {
        logOperation("新增", "设备", "设备名称=" + equipment.getEquipmentName());
        return equipmentMapper.insert(equipment);
    }

    @Override
    public int update(Equipment equipment) {
        logOperation("修改", "设备", "ID=" + equipment.getId());
        return equipmentMapper.update(equipment);
    }

    @Override
    public int updateImage(Integer id, String equipmentImgUrl, Integer updateUserid) {
        logOperation("修改", "设备图片", "ID=" + id);
        return equipmentMapper.updateImage(id, equipmentImgUrl, updateUserid);
    }

    @Override
    public PageResult<Equipment> findPage(int page, int pageSize) {
        List<Equipment> items = equipmentMapper.findPage((page - 1) * pageSize, pageSize);
        long total = equipmentMapper.count();
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    public int deleteById(Integer id, Integer updateUserid) {
        logOperation("删除", "设备", "ID=" + id);
        return equipmentMapper.deleteById(id, updateUserid);
    }
}
