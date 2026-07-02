package com.example.changshademo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Equipment {
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
     * 创建人 ID
     */
    private Integer createUserid;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改人 ID
     */
    private Integer updateUserid;

    /**
     * 设备序号
     */
    private String equipmentSeq;

    /**
     * 设备名称
     */
    private String equipmentName;

    /**
     * 设备图片
     */
    private String equipmentImgUrl;

    /**
     * 设备状态
     * 10：启用
     * 20：停用
     * 30：故障
     */
    private Integer equipmentStatus;

    /**
     * 工厂 ID
     */
    private Integer factoryId;
}
