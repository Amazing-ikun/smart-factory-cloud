package com.example.changshademo.service.impl;

import com.example.changshademo.entity.ProductSchedule;
import com.example.changshademo.enums.ScheduleStatusEnum;
import com.example.changshademo.mapper.ProductScheduleMapper;
import com.example.changshademo.service.BaseService;
import com.example.changshademo.service.ProductScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductScheduleServiceImpl extends BaseService implements ProductScheduleService {
    private final ProductScheduleMapper productScheduleMapper;

    @Override
    public List<ProductSchedule> findAll() {
        logQuery("生产调度");
        return productScheduleMapper.findAll();
    }

    @Override
    public ProductSchedule findById(Integer id) {
        logQuery("生产调度", "ID=" + id);
        return productScheduleMapper.findById(id);
    }

    @Override
    public ProductSchedule findByScheduleSeq(String scheduleSeq) {
        logQuery("生产调度", "工单编号=" + scheduleSeq);
        return productScheduleMapper.findByScheduleSeq(scheduleSeq);
    }

    @Override
    public List<ProductSchedule> findByPlanId(Integer planId) {
        logQuery("生产调度", "计划ID=" + planId);
        return productScheduleMapper.findByPlanId(planId);
    }

    @Override
    public List<ProductSchedule> findByProductId(Integer productId) {
        logQuery("生产调度", "产品ID=" + productId);
        return productScheduleMapper.findByProductId(productId);
    }

    @Override
    public List<ProductSchedule> findByEquipmentId(Integer equipmentId) {
        logQuery("生产调度", "设备ID=" + equipmentId);
        return productScheduleMapper.findByEquipmentId(equipmentId);
    }

    @Override
    public List<ProductSchedule> findByFactoryId(Integer factoryId) {
        logQuery("生产调度", "工厂ID=" + factoryId);
        return productScheduleMapper.findByFactoryId(factoryId);
    }

    @Override
    public List<ProductSchedule> findByScheduleStatus(Integer scheduleStatus) {
        logQuery("生产调度", "状态=" + scheduleStatus);
        return productScheduleMapper.findByScheduleStatus(scheduleStatus);
    }

    @Override
    public List<ProductSchedule> findByDateRange(LocalDate startDate, LocalDate endDate) {
        logQuery("生产调度", "时间范围=" + startDate + " 至 " + endDate);
        return productScheduleMapper.findByDateRange(startDate, endDate);
    }

    @Override
    @Transactional
    public int insert(ProductSchedule productSchedule) {
        if (productSchedule.getScheduleSeq() == null || productSchedule.getScheduleSeq().isEmpty()) {
            throw new RuntimeException("工单编号不能为空");
        }
        if (productSchedule.getPlanId() == null) {
            throw new RuntimeException("计划ID不能为空");
        }
        if (productSchedule.getProductId() == null) {
            throw new RuntimeException("产品ID不能为空");
        }
        if (productSchedule.getScheduleCount() == null || productSchedule.getScheduleCount() <= 0) {
            throw new RuntimeException("工单数量必须大于0");
        }
        if (productSchedule.getStartDate() == null) {
            throw new RuntimeException("开始日期不能为空");
        }
        if (productSchedule.getEndDate() == null) {
            throw new RuntimeException("结束日期不能为空");
        }
        if (productSchedule.getEndDate().isBefore(productSchedule.getStartDate())) {
            throw new RuntimeException("结束日期不能早于开始日期");
        }

        if (productSchedule.getScheduleStatus() == null) {
            productSchedule.setScheduleStatus(ScheduleStatusEnum.NOT_STARTED.getCode());
        }

        logOperation("新增", "生产调度", "工单编号=" + productSchedule.getScheduleSeq());
        return productScheduleMapper.insert(productSchedule);
    }

    @Override
    @Transactional
    public int update(ProductSchedule productSchedule) {
        if (productSchedule.getId() == null) {
            throw new RuntimeException("调度ID不能为空");
        }
        logOperation("更新", "生产调度", "ID=" + productSchedule.getId());
        return productScheduleMapper.update(productSchedule);
    }

    @Override
    @Transactional
    public int deleteById(Integer id, Integer updateUserid) {
        if (id == null) {
            throw new RuntimeException("调度ID不能为空");
        }
        logOperation("删除", "生产调度", "ID=" + id);
        return productScheduleMapper.deleteById(id, updateUserid);
    }
}
