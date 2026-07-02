package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.PageResult;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.BusinessLog;
import com.example.changshademo.entity.User;
import com.example.changshademo.service.BusinessLogService;
import com.example.changshademo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class BusinessLogController {

    private final BusinessLogService businessLogService;
    private final UserService userService;

    @GetMapping("/list")
    public Result<PageResult<BusinessLog>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer pageSize) {

        Integer userId = CurrentUserUtils.getCurrentUserId();
        User currentUser = userService.findById(userId);
        if (currentUser == null || currentUser.getRoleId() == null || currentUser.getRoleId() != 1) {
            return Result.error("仅管理员可查看日志");
        }

        return Result.success(businessLogService.findPage(page, pageSize));
    }
}
