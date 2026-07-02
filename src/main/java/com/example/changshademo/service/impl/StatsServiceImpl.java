package com.example.changshademo.service.impl;

import com.example.changshademo.entity.DailyWork;
import com.example.changshademo.entity.Equipment;
import com.example.changshademo.entity.ProductOrder;
import com.example.changshademo.entity.ProductSchedule;
import com.example.changshademo.mapper.DailyWorkMapper;
import com.example.changshademo.mapper.EquipmentMapper;
import com.example.changshademo.mapper.ProductOrderMapper;
import com.example.changshademo.mapper.ProductScheduleMapper;
import com.example.changshademo.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final EquipmentMapper equipmentMapper;
    private final ProductScheduleMapper productScheduleMapper;
    private final DailyWorkMapper dailyWorkMapper;
    private final ProductOrderMapper productOrderMapper;

    @Override
    public Map<String, Object> getDashboardStats() {
        // 开机率：启用设备数 / 总设备数
        List<Equipment> equipments = equipmentMapper.findAll();
        long totalEquipment = equipments.size();
        long enabledCount = equipments.stream()
                .filter(e -> e.getEquipmentStatus() != null && e.getEquipmentStatus() == 10)
                .count();
        long faultCount = equipments.stream()
                .filter(e -> e.getEquipmentStatus() != null && e.getEquipmentStatus() == 30)
                .count();
        double uptimeRate = totalEquipment > 0 ? round1(enabledCount * 100.0 / totalEquipment) : 0;
        double failureRate = totalEquipment > 0 ? round1(faultCount * 100.0 / totalEquipment) : 0;

        // 运行率：生产中或已完成的调度 / 总调度数
        List<ProductSchedule> schedules = productScheduleMapper.findAll();
        long totalSchedules = schedules.size();
        long runningCount = schedules.stream()
                .filter(s -> s.getScheduleStatus() != null && (s.getScheduleStatus() == 20 || s.getScheduleStatus() == 30))
                .count();
        double operationRate = totalSchedules > 0 ? round1(runningCount * 100.0 / totalSchedules) : 0;

        // 综合率：合格总数 / 加工总数
        List<DailyWork> dailyWorks = dailyWorkMapper.findAll();
        long totalWorking = dailyWorks.stream().mapToLong(d -> d.getWorkingCount() != null ? d.getWorkingCount() : 0).sum();
        long totalQualified = dailyWorks.stream().mapToLong(d -> d.getQualifiedCount() != null ? d.getQualifiedCount() : 0).sum();
        double compositeRate = totalWorking > 0 ? round1(totalQualified * 100.0 / totalWorking) : 0;

        // ---- Chart data ----
        // Bar chart: orders by status
        List<ProductOrder> allOrders = productOrderMapper.findAll();
        Map<Integer, Long> ordersByStatus = allOrders.stream()
                .filter(o -> o.getOrderStatus() != null)
                .collect(Collectors.groupingBy(ProductOrder::getOrderStatus, Collectors.counting()));
        Map<String, Long> barChartData = new LinkedHashMap<>();
        barChartData.put("未接单", ordersByStatus.getOrDefault(10, 0L));
        barChartData.put("已接单", ordersByStatus.getOrDefault(20, 0L));
        barChartData.put("已拒绝", ordersByStatus.getOrDefault(30, 0L));
        barChartData.put("生产中", ordersByStatus.getOrDefault(40, 0L));
        barChartData.put("订单完成", ordersByStatus.getOrDefault(50, 0L));

        // Pie chart: orders by factory
        Map<Integer, Long> ordersByFactory = allOrders.stream()
                .filter(o -> o.getFactoryId() != null)
                .collect(Collectors.groupingBy(ProductOrder::getFactoryId, Collectors.counting()));
        List<Map<String, Object>> pieChartData = ordersByFactory.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", "工厂" + e.getKey());
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        if (pieChartData.isEmpty()) {
            Map<String, Object> emptyItem = new LinkedHashMap<>();
            emptyItem.put("name", "暂无数据");
            emptyItem.put("value", 0);
            pieChartData.add(emptyItem);
        }

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("uptimeRate", uptimeRate);
        stats.put("failureRate", failureRate);
        stats.put("operationRate", operationRate);
        stats.put("compositeRate", compositeRate);
        stats.put("barChartData", barChartData);
        stats.put("pieChartData", pieChartData);
        return stats;
    }

    private static double round1(double val) {
        return Math.round(val * 10.0) / 10.0;
    }
}
