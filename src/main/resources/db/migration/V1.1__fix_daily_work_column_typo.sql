USE `db_factory`;

ALTER TABLE `t_daily_work`
    CHANGE COLUMN `unqualified_cout` `unqualified_count` int(11) DEFAULT NULL COMMENT '不合格数量';
