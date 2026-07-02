package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.PageResult;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.ProductOrder;
import com.example.changshademo.enums.OrderStatusEnum;
import com.example.changshademo.service.ProductOrderService;
import com.example.changshademo.service.impl.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class ProductOrderController {
    private final ProductOrderService productOrderService;
    private final ExcelService excelService;

    @GetMapping("/list")
    public Result<PageResult<ProductOrder>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(productOrderService.findPage(page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ProductOrder> getById(@PathVariable Integer id) {
        ProductOrder order = productOrderService.findById(id);
        if (order != null) {
            return Result.success(order);
        }
        return Result.error("订单不存在");
    }

    @GetMapping("/seq/{orderSeq}")
    public Result<ProductOrder> getByOrderSeq(@PathVariable String orderSeq) {
        ProductOrder order = productOrderService.findByOrderSeq(orderSeq);
        if (order != null) {
            return Result.success(order);
        }
        return Result.error("订单不存在");
    }

    @GetMapping("/factory/{factoryId}")
    public Result<List<ProductOrder>> getByFactoryId(@PathVariable Integer factoryId) {
        return Result.success(productOrderService.findByFactoryId(factoryId));
    }

    @GetMapping("/status/{status}")
    public Result<List<ProductOrder>> getByOrderStatus(@PathVariable Integer status) {
        return Result.success(productOrderService.findByOrderStatus(status));
    }

    @GetMapping("/product/{productId}")
    public Result<List<ProductOrder>> getByProductId(@PathVariable Integer productId) {
        return Result.success(productOrderService.findByProductId(productId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody ProductOrder productOrder) {
        productOrder.setCreateUserid(CurrentUserUtils.getCurrentUserId());
        productOrderService.insert(productOrder);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody ProductOrder productOrder) {
        productOrder.setUpdateUserid(CurrentUserUtils.getCurrentUserId());
        productOrderService.update(productOrder);
        return Result.success("更新成功");
    }

    @PatchMapping("/{id}/status")
    public Result<Map<String, Object>> updateStatus(@PathVariable Integer id,
                                                    @RequestParam Integer status) {
        productOrderService.updateStatus(id, status, CurrentUserUtils.getCurrentUserId());
        Map<String, Object> extra = new HashMap<>();
        extra.put("status", status);
        OrderStatusEnum statusEnum = OrderStatusEnum.fromCode(status);
        if (statusEnum != null) {
            extra.put("statusDesc", statusEnum.getDescription());
        }
        return Result.success(extra, "状态更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        productOrderService.deleteById(id, CurrentUserUtils.getCurrentUserId());
        return Result.success("删除成功");
    }

    @GetMapping("/status-enums")
    public Result<List<OrderStatusEnum>> getStatusEnums() {
        return Result.success(List.of(OrderStatusEnum.values()));
    }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("订单数据.xlsx", "UTF-8"));
        response.getOutputStream().write(excelService.exportOrders());
    }

    @PostMapping("/import")
    public Result<String> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        int count = excelService.importOrders(file);
        return Result.success("成功导入 " + count + " 条订单数据");
    }
}
