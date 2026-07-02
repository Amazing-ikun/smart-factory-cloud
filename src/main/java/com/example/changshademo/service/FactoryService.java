package com.example.changshademo.service;

import com.example.changshademo.entity.Factory;

import java.util.List;

public interface FactoryService {
    List<Factory> findAll();
    Factory findById(Integer id);
    List<Factory> findByName(String name);
}
