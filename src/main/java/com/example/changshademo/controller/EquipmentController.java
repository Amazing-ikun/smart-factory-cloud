package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.PageResult;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.Equipment;
import com.example.changshademo.enums.EquipmentStatusEnum;
import com.example.changshademo.service.EquipmentService;
import com.example.changshademo.service.impl.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;
    private final ExcelService excelService;

    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        if (page != null) {
            return Result.success(equipmentService.findPage(page, pageSize));
        }
        return Result.success(equipmentService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Equipment> getById(@PathVariable Integer id) {
        Equipment equipment = equipmentService.findById(id);
        if (equipment != null) {
            return Result.success(equipment);
        }
        return Result.error("设备不存在");
    }

    @GetMapping("/factory/{factoryId}")
    public Result<List<Equipment>> getByFactoryId(@PathVariable Integer factoryId) {
        return Result.success(equipmentService.findByFactoryId(factoryId));
    }

    @GetMapping("/status/{status}")
    public Result<List<Equipment>> getByStatus(@PathVariable Integer status) {
        return Result.success(equipmentService.findByStatus(status));
    }

    @GetMapping("/status-enums")
    public Result<List<EquipmentStatusEnum>> getStatusEnums() {
        return Result.success(List.of(EquipmentStatusEnum.values()));
    }

    @PostMapping
    public Result<Integer> add(@RequestBody Equipment equipment) {
        equipment.setCreateUserid(CurrentUserUtils.getCurrentUserId());
        equipmentService.insert(equipment);
        return Result.success(equipment.getId(), "添加成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody Equipment equipment) {
        equipment.setUpdateUserid(CurrentUserUtils.getCurrentUserId());
        equipmentService.update(equipment);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        equipmentService.deleteById(id, CurrentUserUtils.getCurrentUserId());
        return Result.success("删除成功");
    }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("设备数据.xlsx", "UTF-8"));
        response.getOutputStream().write(excelService.exportEquipments());
    }

    @PostMapping("/import")
    public Result<String> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        int count = excelService.importEquipments(file);
        return Result.success("成功导入 " + count + " 条设备数据");
    }

    @PostMapping("/upload/{id}")
    public Result<String> uploadImage(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        try {
            String baseDir = System.getProperty("user.dir");
            Path dirPath = Paths.get(baseDir, "uploads", "equipment", String.valueOf(id));
            Files.createDirectories(dirPath);

            String ext = "";
            String originalName = file.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + ext;
            Path targetPath = dirPath.resolve(fileName);
            file.transferTo(targetPath.toFile());

            String urlPath = "/upload/equipment/" + id + "/" + fileName;

            Equipment equipment = equipmentService.findById(id);
            equipment.setEquipmentImgUrl(urlPath);
            equipment.setUpdateUserid(CurrentUserUtils.getCurrentUserId());
            equipmentService.updateImage(id, urlPath, CurrentUserUtils.getCurrentUserId());

            return Result.success(urlPath, "上传成功");
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
