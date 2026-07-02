package com.example.changshademo.service.impl;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.Product;
import com.example.changshademo.mapper.ProductMapper;
import com.example.changshademo.service.BaseService;
import com.example.changshademo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends BaseService implements ProductService {
    private final ProductMapper productMapper;

    @Override
    public List<Product> findAll() {
        logQuery("产品");
        return productMapper.findAll();
    }

    @Override
    public Product findById(Integer id) {
        logQuery("产品", "ID=" + id);
        return productMapper.findById(id);
    }

    @Override
    public List<Product> findByFactoryId(Integer factoryId) {
        logQuery("产品", "工厂ID=" + factoryId);
        return productMapper.findByFactoryId(factoryId);
    }

    @Override
    public List<Product> findByProductName(String productName) {
        logQuery("产品", "关键词=" + productName);
        return productMapper.findByProductName(productName);
    }

    @Override
    public int insert(Product product) {
        if (product.getProductNum() == null || product.getProductNum().isEmpty()) {
            throw new RuntimeException("产品编号不能为空");
        }
        if (product.getProductName() == null || product.getProductName().isEmpty()) {
            throw new RuntimeException("产品名称不能为空");
        }
        logOperation("新增", "产品", "名称=" + product.getProductName());
        return productMapper.insert(product);
    }

    @Override
    public int update(Product product) {
        if (product.getId() == null) {
            throw new RuntimeException("产品ID不能为空");
        }
        logOperation("更新", "产品", "ID=" + product.getId());
        return productMapper.update(product);
    }

    @Override
    public PageResult<Product> findPage(int page, int pageSize) {
        List<Product> items = productMapper.findPage((page - 1) * pageSize, pageSize);
        long total = productMapper.count();
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    public int deleteById(Integer id, Integer updateUserid) {
        if (id == null) {
            throw new RuntimeException("产品ID不能为空");
        }
        logOperation("删除", "产品", "ID=" + id);
        return productMapper.deleteById(id, updateUserid);
    }
}
