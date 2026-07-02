package com.example.changshademo.config;

import com.example.changshademo.entity.User;
import com.example.changshademo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        // 0. 创建业务日志表（如果不存在）
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS t_business_log (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    user_id INT NOT NULL," +
            "    user_name VARCHAR(50) NOT NULL," +
            "    operation VARCHAR(20) NOT NULL COMMENT '查询/新增/修改/删除/登录'," +
            "    module VARCHAR(50) NOT NULL COMMENT '订单/设备/产品/用户/工厂等'," +
            "    detail VARCHAR(500)," +
            "    create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "    INDEX idx_bl_create_time (create_time)" +
            ") COMMENT '业务操作日志表'"
        );
        log.info("已检查/创建业务日志表 t_business_log");

        // 0.1 创建反馈表
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS t_feedback (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    user_id INT NOT NULL," +
            "    user_name VARCHAR(50) NOT NULL," +
            "    content TEXT NOT NULL," +
            "    attachments VARCHAR(1000) DEFAULT '' COMMENT '附件路径,逗号分隔'," +
            "    feedback_type VARCHAR(20) DEFAULT 'suggestion' COMMENT 'suggestion/bug'," +
            "    create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "    reply_content TEXT," +
            "    reply_time DATETIME," +
            "    reply_user_id INT," +
            "    reply_user_name VARCHAR(50)" +
            ") COMMENT '用户反馈表'"
        );
        log.info("已检查/创建反馈表 t_feedback");

        // 0.2 创建管理员申请表
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS t_admin_application (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    user_id INT NOT NULL," +
            "    user_name VARCHAR(50) NOT NULL," +
            "    user_real_name VARCHAR(50) DEFAULT '' COMMENT '真实姓名'," +
            "    reason TEXT NOT NULL COMMENT '申请理由'," +
            "    commitment TEXT COMMENT '个人承诺'," +
            "    phone VARCHAR(20) DEFAULT '' COMMENT '联系方式'," +
            "    email VARCHAR(100) DEFAULT '' COMMENT '邮箱'," +
            "    status INT DEFAULT 0 COMMENT '0:待审核 1:已通过 2:已拒绝'," +
            "    create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "    reply_content TEXT," +
            "    reply_time DATETIME," +
            "    reply_user_id INT," +
            "    reply_user_name VARCHAR(50)" +
            ") COMMENT '管理员申请表'"
        );
        log.info("已检查/创建管理员申请表 t_admin_application");

        // 0.3 创建消息表
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS t_message (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    from_user_id INT NOT NULL," +
            "    from_user_name VARCHAR(50) NOT NULL," +
            "    to_user_id INT NOT NULL," +
            "    title VARCHAR(200) DEFAULT ''," +
            "    content TEXT," +
            "    msg_type VARCHAR(30) DEFAULT 'system' COMMENT 'feedback_reply/application_reply/system'," +
            "    related_id INT DEFAULT 0," +
            "    is_read INT DEFAULT 0 COMMENT '0:未读 1:已读'," +
            "    create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "    INDEX idx_msg_to (to_user_id, is_read)" +
            ") COMMENT '消息表'"
        );
        log.info("已检查/创建消息表 t_message");

        // 1. 创建测试用户（如果不存在）
        if (userMapper.findByUserName("admin") == null) {
            User admin = new User();
            admin.setUserName("admin");
            admin.setUserRealName("管理员");
            admin.setUserPasswd(passwordEncoder.encode("admin123"));
            admin.setRoleId(1);
            admin.setFactoryId(1);
            admin.setUserStatus(0);
            admin.setCreateUserid(0);
            userMapper.insert(admin);
            log.info("已创建管理员账号: admin / admin123");
        }
        if (userMapper.findByUserName("user") == null) {
            User user = new User();
            user.setUserName("user");
            user.setUserRealName("张三");
            user.setUserPasswd(passwordEncoder.encode("user123"));
            user.setRoleId(2);
            user.setFactoryId(1);
            user.setUserStatus(0);
            user.setCreateUserid(0);
            userMapper.insert(user);
            log.info("已创建普通用户账号: user / user123");
        }

        // 2. 升级已有用户的明文密码为 BCrypt
        List<User> users = userMapper.findAll();
        for (User u : users) {
            if (u.getUserPasswd() != null && !u.getUserPasswd().startsWith("$2a$")) {
                String encoded = passwordEncoder.encode(u.getUserPasswd());
                userMapper.updatePassword(u.getId(), encoded, u.getId());
                log.info("用户密码已升级为 BCrypt: {}", u.getUserName());
            }
        }
    }
}
