package com.example.changshademo.service.impl;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.ProductOrder;
import com.example.changshademo.enums.OrderStatusEnum;
import com.example.changshademo.mapper.ProductOrderMapper;
import com.example.changshademo.service.BaseService;
import com.example.changshademo.service.ProductOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductOrderServiceImpl extends BaseService implements ProductOrderService {
    private final ProductOrderMapper productOrderMapper;

    @Override
    public List<ProductOrder> findAll() {
        logQuery("订单");
        return productOrderMapper.findAll();
    }

    @Override
    public PageResult<ProductOrder> findPage(int page, int pageSize) {
        List<ProductOrder> items = productOrderMapper.findPage((page - 1) * pageSize, pageSize);
        long total = productOrderMapper.count();
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    public ProductOrder findById(Integer id) {
        logQuery("订单", "ID=" + id);
        return productOrderMapper.findById(id);
    }

    @Override
    public ProductOrder findByOrderSeq(String orderSeq) {
        logQuery("订单", "订单ID=" + orderSeq);
        return productOrderMapper.findByOrderSeq(orderSeq);
    }

    @Override
    public List<ProductOrder> findByFactoryId(Integer factoryId) {
        logQuery("订单", "工厂Id=" + factoryId);
        return productOrderMapper.findByFactoryId(factoryId);
    }

    @Override
    public List<ProductOrder> findByOrderStatus(Integer orderStatus) {
        logQuery("订单", "订单状态=" + orderStatus);
        return productOrderMapper.findByOrderStatus(orderStatus);
    }

    @Override
    public List<ProductOrder> findByProductId(Integer productId) {
        logQuery("订单", "产品ID=" + productId);
        return productOrderMapper.findByProductId(productId);
    }

    @Override
    @Transactional
    public int insert(ProductOrder productOrder) {
        if (productOrder.getOrderSeq() == null || productOrder.getOrderSeq().isEmpty()) {
            logOperation("新增", "订单", "失败：订单编号为空");
            throw new RuntimeException("订单编号不能为空");
        }
        if (productOrder.getProductId() == null) {
            logOperation("新增", "订单", "失败：产品ID为空，订单编号=" + productOrder.getOrderSeq());
            throw new RuntimeException("产品ID不能为空");
        }
        if (productOrder.getProductCount() == null || productOrder.getProductCount() <= 0) {
            logOperation("新增", "订单", "失败：产品数量无效，订单编号=" + productOrder.getOrderSeq());
            throw new RuntimeException("产品数量必须大于0");
        }
        if (productOrder.getEndDate() == null) {
            logOperation("新增", "订单", "失败：截止日期为空，订单编号=" + productOrder.getOrderSeq());
            throw new RuntimeException("订单截止日期不能为空");
        }
        if (productOrder.getEndDate().isBefore(LocalDate.now())) {
            logOperation("新增", "订单", "失败：截止日期早于当前日期，订单编号=" + productOrder.getOrderSeq());
            throw new RuntimeException("订单截止日期不能早于当前日期");
        }

        if (productOrder.getOrderStatus() == null) {
            productOrder.setOrderStatus(OrderStatusEnum.UNACCEPTED.getCode());
        }

        int result = productOrderMapper.insert(productOrder);
        logOperation("新增", "订单", "成功：订单编号=" + productOrder.getOrderSeq() + "，数量=" + productOrder.getProductCount());
        return result;
    }

    @Override
    @Transactional
    public int update(ProductOrder productOrder) {
        if (productOrder.getId() == null) {
            logOperation("更新", "订单", "失败：订单ID为空");
            throw new RuntimeException("订单ID不能为空");
        }

        ProductOrder existingOrder = productOrderMapper.findById(productOrder.getId());
        if (existingOrder == null) {
            logOperation("更新", "订单", "失败：订单不存在，ID=" + productOrder.getId());
            throw new RuntimeException("订单不存在");
        }

        if (productOrder.getOrderStatus() != null &&
                !productOrder.getOrderStatus().equals(existingOrder.getOrderStatus())) {

            OrderStatusEnum currentStatus = OrderStatusEnum.fromCode(existingOrder.getOrderStatus());
            OrderStatusEnum targetStatus = OrderStatusEnum.fromCode(productOrder.getOrderStatus());

            if (currentStatus != null && targetStatus != null) {
                if (!currentStatus.canTransitionTo(targetStatus)) {
                    logOperation("更新", "订单", "失败：状态流转不合法，订单编号=" + existingOrder.getOrderSeq() +
                            "，" + currentStatus.getDescription() + " → " + targetStatus.getDescription());
                    throw new RuntimeException("不允许从 " + currentStatus.getDescription() +
                            " 状态转为 " + targetStatus.getDescription() + " 状态");
                }
            }
        }

        int result = productOrderMapper.update(productOrder);
        logOperation("更新", "订单", "成功：订单编号=" + existingOrder.getOrderSeq() + "，状态=" +
                OrderStatusEnum.fromCode(productOrder.getOrderStatus()).getDescription());
        return result;
    }

    @Override
    @Transactional
    public int updateStatus(Integer id, Integer orderStatus, Integer updateUserid) {
        if (id == null) {
            logOperation("更新状态", "订单", "失败：订单ID为空");
            throw new RuntimeException("订单ID不能为空");
        }
        if (orderStatus == null) {
            logOperation("更新状态", "订单", "失败：订单状态为空，ID=" + id);
            throw new RuntimeException("订单状态不能为空");
        }

        ProductOrder existingOrder = productOrderMapper.findById(id);
        if (existingOrder == null) {
            logOperation("更新状态", "订单", "失败：订单不存在，ID=" + id);
            throw new RuntimeException("订单不存在");
        }

        OrderStatusEnum currentStatus = OrderStatusEnum.fromCode(existingOrder.getOrderStatus());
        OrderStatusEnum targetStatus = OrderStatusEnum.fromCode(orderStatus);

        if (currentStatus != null && targetStatus != null) {
            if (!currentStatus.canTransitionTo(targetStatus)) {
                logOperation("更新状态", "订单", "失败：状态流转不合法，订单编号=" + existingOrder.getOrderSeq() +
                        "，" + currentStatus.getDescription() + " → " + targetStatus.getDescription());
                throw new RuntimeException("不允许从 " + currentStatus.getDescription() +
                        " 状态转为 " + targetStatus.getDescription() + " 状态");
            }
        }

        int result = productOrderMapper.updateStatus(id, orderStatus, updateUserid);
        logOperation("更新状态", "订单", "成功：订单编号=" + existingOrder.getOrderSeq() +
                "，" + currentStatus.getDescription() + " → " + targetStatus.getDescription());
        return result;
    }

    @Override
    @Transactional
    public int deleteById(Integer id, Integer updateUserid) {
        if (id == null) {
            logOperation("删除", "订单", "失败：订单ID为空");
            throw new RuntimeException("订单ID不能为空");
        }

        ProductOrder existingOrder = productOrderMapper.findById(id);
        String orderInfo = (existingOrder != null) ? "订单编号=" + existingOrder.getOrderSeq() : "ID=" + id;

        int result = productOrderMapper.deleteById(id, updateUserid);
        if (result > 0) {
            logOperation("删除", "订单", "成功：" + orderInfo);
        } else {
            logOperation("删除", "订单", "失败：记录不存在，" + orderInfo);
        }
        return result;
    }
}
