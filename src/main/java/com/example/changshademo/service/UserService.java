package com.example.changshademo.service;

import com.example.changshademo.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Integer id);
    User findByUserName(String userName);
    List<User> findByFactoryId(Integer factoryId);
    List<User> findByStatus(Integer status);
    int insert(User user);
    int updateIntro(Integer id, String userIntro, Integer userId);
    int updateAvatar(Integer id, String userAvatar, Integer userId);
    int updateUser(User user);
    int deleteById(Integer id, Integer operatorId);
    int updatePassword(Integer id, String newPassword, Integer operatorId);
    int updateRole(Integer id, Integer roleId, Integer operatorId);
    com.example.changshademo.common.PageResult<com.example.changshademo.entity.User> findPage(int page, int pageSize);
}
