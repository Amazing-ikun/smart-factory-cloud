package com.example.changshademo.service.impl;

import com.example.changshademo.entity.Equipment;
import com.example.changshademo.entity.Product;
import com.example.changshademo.entity.ProductOrder;
import com.example.changshademo.mapper.EquipmentMapper;
import com.example.changshademo.mapper.ProductMapper;
import com.example.changshademo.mapper.ProductOrderMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final ProductMapper productMapper;
    private final EquipmentMapper equipmentMapper;
    private final ProductOrderMapper productOrderMapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] PRODUCT_HEADERS = {"ID", "产品编号", "产品名称", "工厂ID", "创建时间"};
    private static final String[] EQUIPMENT_HEADERS = {"ID", "设备编号", "设备名称", "设备状态", "工厂ID", "创建时间"};
    private static final String[] ORDER_HEADERS = {"ID", "订单编号", "产品ID", "产品数量", "订单状态", "工厂ID", "截止日期", "创建时间"};

    // ==================== Export ====================

    public byte[] exportProducts() throws Exception {
        List<Product> list = productMapper.findAll();
        return writeExcel("产品数据", PRODUCT_HEADERS, (sheet, rowNum) -> {
            for (Product p : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0, CellType.NUMERIC).setCellValue(p.getId());
                row.createCell(1, CellType.STRING).setCellValue(nullSafe(p.getProductNum()));
                row.createCell(2, CellType.STRING).setCellValue(nullSafe(p.getProductName()));
                row.createCell(3, CellType.NUMERIC).setCellValue(nullSafeInt(p.getFactoryId()));
                row.createCell(4, CellType.STRING).setCellValue(fmtTime(p.getCreateTime()));
            }
        });
    }

    public byte[] exportEquipments() throws Exception {
        List<Equipment> list = equipmentMapper.findAll();
        return writeExcel("设备数据", EQUIPMENT_HEADERS, (sheet, rowNum) -> {
            for (Equipment e : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0, CellType.NUMERIC).setCellValue(e.getId());
                row.createCell(1, CellType.STRING).setCellValue(nullSafe(e.getEquipmentSeq()));
                row.createCell(2, CellType.STRING).setCellValue(nullSafe(e.getEquipmentName()));
                row.createCell(3, CellType.NUMERIC).setCellValue(nullSafeInt(e.getEquipmentStatus()));
                row.createCell(4, CellType.NUMERIC).setCellValue(nullSafeInt(e.getFactoryId()));
                row.createCell(5, CellType.STRING).setCellValue(fmtTime(e.getCreateTime()));
            }
        });
    }

    public byte[] exportOrders() throws Exception {
        List<ProductOrder> list = productOrderMapper.findAll();
        return writeExcel("订单数据", ORDER_HEADERS, (sheet, rowNum) -> {
            for (ProductOrder o : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0, CellType.NUMERIC).setCellValue(o.getId());
                row.createCell(1, CellType.STRING).setCellValue(nullSafe(o.getOrderSeq()));
                row.createCell(2, CellType.NUMERIC).setCellValue(nullSafeInt(o.getProductId()));
                row.createCell(3, CellType.NUMERIC).setCellValue(nullSafeInt(o.getProductCount()));
                row.createCell(4, CellType.NUMERIC).setCellValue(nullSafeInt(o.getOrderStatus()));
                row.createCell(5, CellType.NUMERIC).setCellValue(nullSafeInt(o.getFactoryId()));
                row.createCell(6, CellType.STRING).setCellValue(o.getEndDate() != null ? o.getEndDate().toString() : "");
                row.createCell(7, CellType.STRING).setCellValue(fmtTime(o.getCreateTime()));
            }
        });
    }

    // ==================== Import ====================

    public int importProducts(MultipartFile file) throws Exception {
        int count = 0;
        for (Row row : readRows(file, 0)) {
            if (row.getRowNum() == 0) continue; // skip header
            String productNum = getCellString(row.getCell(1));
            String productName = getCellString(row.getCell(2));
            if (productNum.isEmpty() || productName.isEmpty()) continue;
            Integer factoryId = (int) getCellNumeric(row.getCell(3));

            Product p = new Product();
            p.setProductNum(productNum);
            p.setProductName(productName);
            p.setFactoryId(factoryId);
            p.setCreateUserid(0);
            productMapper.insert(p);
            count++;
        }
        return count;
    }

    public int importEquipments(MultipartFile file) throws Exception {
        int count = 0;
        for (Row row : readRows(file, 0)) {
            if (row.getRowNum() == 0) continue;
            String seq = getCellString(row.getCell(1));
            String name = getCellString(row.getCell(2));
            if (seq.isEmpty() || name.isEmpty()) continue;

            Equipment e = new Equipment();
            e.setEquipmentSeq(seq);
            e.setEquipmentName(name);
            e.setEquipmentStatus((int) getCellNumeric(row.getCell(3)));
            e.setFactoryId((int) getCellNumeric(row.getCell(4)));
            e.setCreateUserid(0);
            equipmentMapper.insert(e);
            count++;
        }
        return count;
    }

    public int importOrders(MultipartFile file) throws Exception {
        int count = 0;
        for (Row row : readRows(file, 0)) {
            if (row.getRowNum() == 0) continue;
            String orderSeq = getCellString(row.getCell(1));
            if (orderSeq.isEmpty()) continue;

            ProductOrder o = new ProductOrder();
            o.setOrderSeq(orderSeq);
            o.setProductId((int) getCellNumeric(row.getCell(2)));
            o.setProductCount((int) getCellNumeric(row.getCell(3)));
            o.setOrderStatus((int) getCellNumeric(row.getCell(4)));
            o.setFactoryId((int) getCellNumeric(row.getCell(5)));
            o.setCreateUserid(0);
            productOrderMapper.insert(o);
            count++;
        }
        return count;
    }

    // ==================== Helpers ====================

    @FunctionalInterface
    private interface SheetFiller {
        void fill(Sheet sheet, int startRow) throws Exception;
    }

    private byte[] writeExcel(String sheetName, String[] headers, SheetFiller filler) throws Exception {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet(sheetName);
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }
            filler.fill(sheet, 1);
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            wb.write(os);
            return os.toByteArray();
        }
    }

    private Iterable<Row> readRows(MultipartFile file, int sheetIndex) throws Exception {
        Workbook wb = new XSSFWorkbook(file.getInputStream());
        return wb.getSheetAt(sheetIndex);
    }

    private String getCellString(Cell c) {
        if (c == null) return "";
        return switch (c.getCellType()) {
            case STRING -> c.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) c.getNumericCellValue());
            default -> "";
        };
    }

    private double getCellNumeric(Cell c) {
        if (c == null) return 0;
        return switch (c.getCellType()) {
            case NUMERIC -> c.getNumericCellValue();
            case STRING -> {
                try { yield Double.parseDouble(c.getStringCellValue().trim()); }
                catch (NumberFormatException e) { yield 0; }
            }
            default -> 0;
        };
    }

    private static String nullSafe(String s) { return s != null ? s : ""; }
    private static int nullSafeInt(Integer v) { return v != null ? v : 0; }
    private static String fmtTime(LocalDateTime t) { return t != null ? t.format(FMT) : ""; }
}
