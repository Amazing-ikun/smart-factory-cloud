package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.EquipmentProduct;
import com.example.changshademo.service.EquipmentProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment-product")
@RequiredArgsConstructor
public class EquipmentProductController {
    private final EquipmentProductService equipmentProductService;

    @GetMapping("/list")
    public Result<List<EquipmentProduct>> list() {
        return Result.success(equipmentProductService.findAll());
    }

    @GetMapping("/{id}")
    public Result<EquipmentProduct> getById(@PathVariable Integer id) {
        EquipmentProduct ep = equipmentProductService.findById(id);
        if (ep != null) {
            return Result.success(ep);
        }
        return Result.error("设备-产品关联不存在");
    }

    @GetMapping("/equipment/{equipmentId}")
    public Result<List<EquipmentProduct>> getByEquipmentId(@PathVariable Integer equipmentId) {
        return Result.success(equipmentProductService.findByEquipmentId(equipmentId));
    }

    @GetMapping("/product/{productId}")
    public Result<List<EquipmentProduct>> getByProductId(@PathVariable Integer productId) {
        return Result.success(equipmentProductService.findByProductId(productId));
    }

    @GetMapping("/factory/{factoryId}")
    public Result<List<EquipmentProduct>> getByFactoryId(@PathVariable Integer factoryId) {
        return Result.success(equipmentProductService.findByFactoryId(factoryId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody EquipmentProduct ep) {
        ep.setCreateUserid(CurrentUserUtils.getCurrentUserId());
        equipmentProductService.insert(ep);
        return Result.success("添加成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        equipmentProductService.deleteById(id, CurrentUserUtils.getCurrentUserId());
        return Result.success("删除成功");
    }
}
