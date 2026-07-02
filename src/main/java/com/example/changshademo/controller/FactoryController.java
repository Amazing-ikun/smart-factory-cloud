package com.example.changshademo.controller;

import com.example.changshademo.common.Result;
import com.example.changshademo.entity.Factory;
import com.example.changshademo.service.FactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factory")
@RequiredArgsConstructor
public class FactoryController {
    private final FactoryService factoryService;

    @GetMapping("/list")
    public Result<List<Factory>> list() {
        return Result.success(factoryService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Factory> getById(@PathVariable Integer id) {
        Factory factory = factoryService.findById(id);
        if (factory != null) {
            return Result.success(factory);
        }
        return Result.error("工厂不存在");
    }

    @GetMapping("/search")
    public Result<List<Factory>> search(@RequestParam String name) {
        return Result.success(factoryService.findByName(name));
    }
}
