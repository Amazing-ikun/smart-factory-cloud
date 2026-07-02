package com.example.changshademo.service.impl;

import com.example.changshademo.entity.Factory;
import com.example.changshademo.mapper.FactoryMapper;
import com.example.changshademo.service.BaseService;
import com.example.changshademo.service.FactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FactoryServiceImpl extends BaseService implements FactoryService {
    private final FactoryMapper factoryMapper;

    @Override
    public List<Factory> findAll() {
        logQuery("工厂");
        return factoryMapper.findAll();
    }

    @Override
    public Factory findById(Integer id) {
        logQuery("工厂", "ID=" + id);
        return factoryMapper.findById(id);
    }

    @Override
    public List<Factory> findByName(String name) {
        logQuery("工厂", "名称=" + name);
        return factoryMapper.findByName(name);
    }
}
