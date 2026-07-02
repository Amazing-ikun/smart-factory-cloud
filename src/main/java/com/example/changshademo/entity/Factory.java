package com.example.changshademo.entity;

import lombok.Data;
import java.time.*;

@Data
public class Factory {
    /**
     * 工厂 ID
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
     * 备注
     */
    private String bak;

    /**
     * 工厂名称
     */
    private String factoryName;

    /**
     * 工厂图片
     */
    private String factoryImgUrl;

    /**
     * 工厂地址
     */
    private String factoryAddr;

    /**
     * 工厂网址
     */
    private String factoryUrl;

    /**
     * 工厂人数
     */
    private Integer factoryWorker;

    /**
     * 工厂状态
     * 0：正常
     * 1：关闭
     */
    private Integer factoryStatus;
}
