-- =====================================================
-- Migration V1.2: Fix index issues & add missing fields
-- Description:
--   1. Fix t_product_plan duplicate/wrong-named index
--   2. Remove t_product duplicate index
--   3. Add soft-delete & audit fields to t_equipment_product
-- =====================================================

USE `db_factory`;

-- 1) t_product_plan: drop the wrongly-named duplicate index (Index_order_seq on plan_seq)
DROP INDEX `Index_order_seq` ON `t_product_plan`;

-- 2) t_product: drop the wrongly-named duplicate index (Index_product_name on product_num, same as Index_product_num)
DROP INDEX `Index_product_name` ON `t_product`;

-- 3) t_equipment_product: add soft-delete and audit fields
ALTER TABLE `t_equipment_product`
    ADD COLUMN `flag`          int(11)   DEFAULT '0' COMMENT '有效标识  0：有效  1：无效' AFTER `id`,
    ADD COLUMN `create_time`   datetime  DEFAULT NULL COMMENT '创建时间'          AFTER `flag`,
    ADD COLUMN `create_userid` int(11)   DEFAULT NULL COMMENT '创建人ID'          AFTER `create_time`,
    ADD COLUMN `update_time`   datetime  DEFAULT NULL COMMENT '修改时间'          AFTER `create_userid`,
    ADD COLUMN `update_userid` int(11)   DEFAULT NULL COMMENT '修改人ID'          AFTER `update_time`;
