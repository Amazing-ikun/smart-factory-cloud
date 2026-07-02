package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.PageResult;
import com.example.changshademo.common.Result;
import com.example.changshademo.dto.LoginRequest;
import com.example.changshademo.entity.User;
import com.example.changshademo.entity.UserAvatarHistory;
import com.example.changshademo.enums.UserStatusEnum;
import com.example.changshademo.mapper.UserAvatarHistoryMapper;
import com.example.changshademo.service.BusinessLogService;
import com.example.changshademo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserAvatarHistoryMapper avatarHistoryMapper;
    private final BusinessLogService businessLogService;

    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        if (page != null) {
            return Result.success(userService.findPage(page, pageSize));
        }
        return Result.success(userService.findAll());
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @GetMapping("/username/{userName}")
    public Result<User> getByUserName(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @GetMapping("/factory/{factoryId}")
    public Result<List<User>> getByFactoryId(@PathVariable Integer factoryId) {
        List<User> users = userService.findByFactoryId(factoryId);
        return Result.success(users);
    }

    @GetMapping("/status/{status}")
    public Result<List<User>> getByStatus(@PathVariable Integer status) {
        List<User> users = userService.findByStatus(status);
        return Result.success(users);
    }

    @GetMapping("/status-enums")
    public Result<List<UserStatusEnum>> getStatusEnums() {
        List<UserStatusEnum> enums = List.of(UserStatusEnum.values());
        return Result.success(enums);
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        User user = userService.findByUserName(request.getUsername().trim());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword().trim(), user.getUserPasswd())) {
            return Result.error("用户名或密码错误");
        }

        if (user.getUserStatus() != null && user.getUserStatus() == 1) {
            return Result.error("用户已被锁定");
        }

        user.setUserPasswd(null);
        businessLogService.addLog(user.getId(), user.getUserName(), "登录", "", "用户登录系统");
        return Result.success(user, "登录成功");
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        if (userService.findByUserName(user.getUserName()) != null) {
            return Result.error("用户名已存在");
        }
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getUserPasswd() == null || user.getUserPasswd().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        user.setCreateUserid(0);
        userService.insert(user);
        return Result.success("注册成功");
    }

    // ====== 个人中心 ======

    @GetMapping("/{id}/profile")
    public Result<User> getProfile(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setUserPasswd(null);
        return Result.success(user);
    }

    @PutMapping("/{id}/intro")
    public Result<Void> updateIntro(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String intro = body.get("userIntro");
        if (intro == null) {
            return Result.error("简介不能为空");
        }
        userService.updateIntro(id, intro, CurrentUserUtils.getCurrentUserId());
        return Result.success("简介更新成功");
    }

    @PostMapping("/{id}/avatar")
    public Result<String> uploadAvatar(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        try {
            String baseDir = System.getProperty("user.dir");
            Path dirPath = Paths.get(baseDir, "uploads", "avatar", String.valueOf(id));
            Files.createDirectories(dirPath);

            String ext = "";
            String originalName = file.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + ext;
            Path targetPath = dirPath.resolve(fileName);
            file.transferTo(targetPath.toFile());

            String urlPath = "/upload/avatar/" + id + "/" + fileName;

            // 将旧头像加入历史记录
            User user = userService.findById(id);
            if (user != null && user.getUserAvatar() != null) {
                UserAvatarHistory history = new UserAvatarHistory();
                history.setUserId(id);
                history.setAvatarUrl(user.getUserAvatar());
                avatarHistoryMapper.insert(history);
            }

            // 更新用户当前头像
            userService.updateAvatar(id, urlPath, CurrentUserUtils.getCurrentUserId());

            return Result.success(urlPath, "头像上传成功");
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/avatar-history")
    public Result<List<UserAvatarHistory>> getAvatarHistory(@PathVariable Integer id) {
        List<UserAvatarHistory> list = avatarHistoryMapper.findByUserId(id);
        return Result.success(list);
    }

    @DeleteMapping("/avatar-history/{historyId}")
    public Result<Void> deleteAvatarHistory(@PathVariable Integer historyId) {
        avatarHistoryMapper.deleteById(historyId);
        return Result.success("删除成功");
    }

    @DeleteMapping("/avatar-history/batch")
    public Result<Void> deleteAvatarHistoryBatch(@RequestBody Map<String, List<Integer>> body) {
        List<Integer> ids = body.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的记录");
        }
        for (Integer id : ids) {
            avatarHistoryMapper.deleteById(id);
        }
        return Result.success("删除成功");
    }

    // ====== 用户管理 (SuperAdmin) ======

    @PostMapping("/create")
    public Result<Integer> createUser(@RequestBody User user) {
        Integer operatorId = CurrentUserUtils.getCurrentUserId();
        if (operatorId != 1) {
            return Result.error("仅超级管理员可创建用户");
        }
        if (userService.findByUserName(user.getUserName()) != null) {
            return Result.error("用户名已存在");
        }
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getUserPasswd() == null || user.getUserPasswd().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        // SuperAdmin (roleId=1, userId=1) 不能创建 SuperAdmin
        if (user.getRoleId() != null && user.getRoleId() == 1) {
            user.setRoleId(1);
        } else {
            user.setRoleId(2);
        }
        user.setCreateUserid(operatorId);
        userService.insert(user);
        return Result.success(user.getId(), "创建成功");
    }

    @PutMapping
    public Result<Void> updateUser(@RequestBody User user) {
        Integer operatorId = CurrentUserUtils.getCurrentUserId();
        if (operatorId != 1) {
            return Result.error("仅超级管理员可修改用户");
        }
        User existing = userService.findById(user.getId());
        if (existing == null) {
            return Result.error("用户不存在");
        }
        // 不能修改 SuperAdmin
        if (existing.getId() == 1) {
            return Result.error("不能修改超级管理员");
        }
        user.setUpdateUserid(operatorId);
        userService.updateUser(user);
        return Result.success("修改成功");
    }

    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Integer operatorId = CurrentUserUtils.getCurrentUserId();
        if (operatorId != 1) {
            return Result.error("仅超级管理员可重置密码");
        }
        User existing = userService.findById(id);
        if (existing == null) {
            return Result.error("用户不存在");
        }
        if (existing.getId() == 1) {
            return Result.error("不能重置超级管理员密码");
        }
        String newPassword = body.get("password");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        userService.updatePassword(id, newPassword.trim(), operatorId);
        return Result.success("密码重置成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id) {
        Integer operatorId = CurrentUserUtils.getCurrentUserId();
        if (operatorId != 1) {
            return Result.error("仅超级管理员可删除用户");
        }
        if (id == 1) {
            return Result.error("不能删除超级管理员");
        }
        User existing = userService.findById(id);
        if (existing == null) {
            return Result.error("用户不存在");
        }
        userService.deleteById(id, operatorId);
        return Result.success("删除成功");
    }
}
