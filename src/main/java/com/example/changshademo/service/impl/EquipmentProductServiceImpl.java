package com.example.changshademo.service.impl;

import com.example.changshademo.entity.EquipmentProduct;
import com.example.changshademo.mapper.EquipmentProductMapper;
import com.example.changshademo.service.BaseService;
import com.example.changshademo.service.EquipmentProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentProductServiceImpl extends BaseService implements EquipmentProductService {
    private final EquipmentProductMapper equipmentProductMapper;

    @Override
    public List<EquipmentProduct> findAll() {
        logQuery("设备产能");
        return equipmentProductMapper.findAll();
    }

    @Override
    public EquipmentProduct findById(Integer id) {
        logQuery("设备产能", "ID=" + id);
        return equipmentProductMapper.findById(id);
    }

    @Override
    public List<EquipmentProduct> findByEquipmentId(Integer equipmentId) {
        logQuery("设备产能", "设备ID=" + equipmentId);
        return equipmentProductMapper.findByEquipmentId(equipmentId);
    }

    @Override
    public List<EquipmentProduct> findByProductId(Integer productId) {
        logQuery("设备产能", "产品ID=" + productId);
        return equipmentProductMapper.findByProductId(productId);
    }

    @Override
    public List<EquipmentProduct> findByFactoryId(Integer factoryId) {
        logQuery("设备产能", "工厂ID=" + factoryId);
        return equipmentProductMapper.findByFactoryId(factoryId);
    }

    @Override
    public int insert(EquipmentProduct ep) {
        logOperation("新增", "设备产能", "设备ID=" + ep.getEquipmentId() + "，产品ID=" + ep.getProductId());
        return equipmentProductMapper.insert(ep);
    }

    @Override
    public void deleteById(Integer id, Integer updateUserid) {
        logOperation("删除", "设备产能", "ID=" + id);
        equipmentProductMapper.deleteById(id, updateUserid);
    }
}
