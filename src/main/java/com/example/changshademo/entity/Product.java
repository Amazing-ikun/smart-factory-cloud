package com.example.changshademo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 产品实体类，对应数据库表 t_product
 */
@Data
public class Product {
    /**
     * ID 标识
     */
    private Integer id;

    /**
     * 有效标识
     * 0：有效
     * 1：无效
     */
    private Integer flag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    private Integer createUserid;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改人ID
     */
    private Integer updateUserid;

    /**
     * 产品编号
     */
    private String productNum;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品图片所在目录
     */
    private String productImgUrl;

    /**
     * 工厂 ID
     */
    private Integer factoryId;
}
