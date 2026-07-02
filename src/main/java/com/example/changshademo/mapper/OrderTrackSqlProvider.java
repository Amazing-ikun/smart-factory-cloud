package com.example.changshademo.mapper;

import java.util.Map;

public class OrderTrackSqlProvider {

    public String findPage(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder("SELECT * FROM t_order_track WHERE flag = 0");
        sql.append(buildWhere(params));
        sql.append(" ORDER BY create_time DESC LIMIT #{offset}, #{limit}");
        return sql.toString();
    }

    public String count(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM t_order_track WHERE flag = 0");
        sql.append(buildWhere(params));
        return sql.toString();
    }

    private StringBuilder buildWhere(Map<String, Object> params) {
        StringBuilder where = new StringBuilder();
        if (params.get("factoryId") != null) {
            where.append(" AND factory_id = #{factoryId}");
        }
        if (params.get("start") != null && params.get("end") != null) {
            where.append(" AND create_time BETWEEN #{start} AND #{end}");
        }
        return where;
    }
}
