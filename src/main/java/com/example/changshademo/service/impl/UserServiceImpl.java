package com.example.changshademo.service.impl;

import com.example.changshademo.common.PageResult;
import com.example.changshademo.entity.User;
import com.example.changshademo.mapper.UserMapper;
import com.example.changshademo.service.BaseService;
import com.example.changshademo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseService implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        logQuery("用户");
        return userMapper.findAll();
    }

    @Override
    public User findById(Integer id) {
        logQuery("用户", "ID=" + id);
        return userMapper.findById(id);
    }

    @Override
    public User findByUserName(String userName) {
        logQuery("用户", "用户名=" + userName);
        return userMapper.findByUserName(userName);
    }

    @Override
    public List<User> findByFactoryId(Integer factoryId) {
        logQuery("用户", "工厂ID=" + factoryId);
        return userMapper.findByFactoryId(factoryId);
    }

    @Override
    public List<User> findByStatus(Integer status) {
        logQuery("用户", "状态=" + status);
        return userMapper.findByStatus(status);
    }

    @Override
    @Transactional
    public int insert(User user) {
        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getUserPasswd() == null || user.getUserPasswd().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        // 密码自动加密
        user.setUserPasswd(passwordEncoder.encode(user.getUserPasswd()));
        logOperation("新增", "用户", "用户名=" + user.getUserName());
        return userMapper.insert(user);
    }

    @Override
    public int updateIntro(Integer id, String userIntro, Integer userId) {
        logOperation("修改", "用户简介", "用户ID=" + id);
        return userMapper.updateIntro(id, userIntro, userId);
    }

    @Override
    public int updateAvatar(Integer id, String userAvatar, Integer userId) {
        logOperation("修改", "用户头像", "用户ID=" + id);
        return userMapper.updateAvatar(id, userAvatar, userId);
    }

    @Override
    public int updateUser(User user) {
        logOperation("修改", "用户", "用户ID=" + user.getId());
        return userMapper.updateUser(user);
    }

    @Override
    public int deleteById(Integer id, Integer operatorId) {
        logOperation("删除", "用户", "用户ID=" + id);
        return userMapper.deleteById(id, operatorId);
    }

    @Override
    public int updatePassword(Integer id, String newPassword, Integer operatorId) {
        logOperation("重置密码", "用户", "用户ID=" + id);
        String encoded = passwordEncoder.encode(newPassword);
        return userMapper.updatePassword(id, encoded, operatorId);
    }

    @Override
    public PageResult<User> findPage(int page, int pageSize) {
        List<User> items = userMapper.findPage((page - 1) * pageSize, pageSize);
        long total = userMapper.count();
        return new PageResult<>(items, total, page, pageSize);
    }

    @Override
    public int updateRole(Integer id, Integer roleId, Integer operatorId) {
        logOperation("修改角色", "用户", "用户ID=" + id + "，角色=" + (roleId == 1 ? "管理员" : "普通用户"));
        return userMapper.updateRole(id, roleId, operatorId);
    }
}
