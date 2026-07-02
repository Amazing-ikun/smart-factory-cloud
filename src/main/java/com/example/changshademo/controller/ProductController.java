package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.PageResult;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.Product;
import com.example.changshademo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.changshademo.service.impl.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ExcelService excelService;

    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        if (page != null) {
            return Result.success(productService.findPage(page, pageSize));
        }
        return Result.success(productService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product != null) {
            return Result.success(product);
        }
        return Result.error("产品不存在");
    }

    @GetMapping("/factory/{factoryId}")
    public Result<List<Product>> getByFactoryId(@PathVariable Integer factoryId) {
        return Result.success(productService.findByFactoryId(factoryId));
    }

    @GetMapping("/search")
    public Result<List<Product>> searchByName(@RequestParam String name) {
        return Result.success(productService.findByProductName(name));
    }

    @PostMapping
    public Result<Integer> add(@RequestBody Product product) {
        product.setCreateUserid(CurrentUserUtils.getCurrentUserId());
        productService.insert(product);
        return Result.success(product.getId(), "添加成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody Product product) {
        product.setUpdateUserid(CurrentUserUtils.getCurrentUserId());
        productService.update(product);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        productService.deleteById(id, CurrentUserUtils.getCurrentUserId());
        return Result.success("删除成功");
    }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("产品数据.xlsx", "UTF-8"));
        response.getOutputStream().write(excelService.exportProducts());
    }

    @PostMapping("/import")
    public Result<String> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        int count = excelService.importProducts(file);
        return Result.success("成功导入 " + count + " 条产品数据");
    }

    @PostMapping("/upload/{id}")
    public Result<String> uploadImage(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        try {
            String baseDir = System.getProperty("user.dir");
            Path dirPath = Paths.get(baseDir, "uploads", "product", String.valueOf(id));
            Files.createDirectories(dirPath);

            String ext = "";
            String originalName = file.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + ext;
            Path targetPath = dirPath.resolve(fileName);
            file.transferTo(targetPath.toFile());

            String urlPath = "/upload/product/" + id + "/" + fileName;

            Product product = productService.findById(id);
            product.setProductImgUrl(urlPath);
            product.setUpdateUserid(CurrentUserUtils.getCurrentUserId());
            productService.update(product);

            return Result.success(urlPath, "上传成功");
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
