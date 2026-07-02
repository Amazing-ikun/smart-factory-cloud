package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.PageResult;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.AdminApplication;
import com.example.changshademo.entity.Message;
import com.example.changshademo.entity.User;
import com.example.changshademo.service.AdminApplicationService;
import com.example.changshademo.service.MessageService;
import com.example.changshademo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class AdminApplicationController {

    private final AdminApplicationService applicationService;
    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/list")
    public Result<PageResult<AdminApplication>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Integer userId = CurrentUserUtils.getCurrentUserId();
        User user = userService.findById(userId);
        if (user == null || user.getRoleId() != 1) {
            return Result.error("仅管理员可查看所有申请");
        }
        return Result.success(applicationService.findPage(page, pageSize));
    }

    @GetMapping("/my")
    public Result<List<AdminApplication>> my() {
        Integer userId = CurrentUserUtils.getCurrentUserId();
        return Result.success(applicationService.findByUserId(userId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody AdminApplication application) {
        Integer userId = CurrentUserUtils.getCurrentUserId();
        User user = userService.findById(userId);
        if (user == null) return Result.error("用户不存在");
        application.setUserId(userId);
        application.setUserName(user.getUserName());
        applicationService.add(application);
        return Result.success("申请已提交");
    }

    @PostMapping("/review")
    public Result<Void> review(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        String action = (String) params.get("action");
        String replyContent = (String) params.get("replyContent");
        Integer userId = CurrentUserUtils.getCurrentUserId();
        User user = userService.findById(userId);
        if (user == null || user.getId() != 1) {
            return Result.error("仅超级管理员可审核");
        }

        int status = "approve".equals(action) ? 1 : 2;

        AdminApplication app = applicationService.findById(id);
        if (app == null) {
            return Result.error("申请记录不存在");
        }

        applicationService.review(id, status, replyContent, userId, user.getUserName());

        Message msg = new Message();
        msg.setFromUserId(userId);
        msg.setFromUserName(user.getUserName());
        msg.setToUserId(app.getUserId());
        msg.setTitle(status == 1 ? "管理员申请已通过" : "管理员申请未通过");
        msg.setContent("您的管理员申请" + (status == 1 ? "已通过" : "未通过") + "。" + (replyContent != null ? replyContent : ""));
        msg.setMsgType("application_reply");
        msg.setRelatedId(id);
        messageService.send(msg);

        if (status == 1) {
            userService.updateRole(app.getUserId(), 1, userId);
        }

        return Result.success("操作成功");
    }
}
