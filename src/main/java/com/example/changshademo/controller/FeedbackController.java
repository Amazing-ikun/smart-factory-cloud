package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.PageResult;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.Feedback;
import com.example.changshademo.entity.Message;
import com.example.changshademo.entity.User;
import com.example.changshademo.service.FeedbackService;
import com.example.changshademo.service.MessageService;
import com.example.changshademo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/list")
    public Result<PageResult<Feedback>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Integer userId = CurrentUserUtils.getCurrentUserId();
        User user = userService.findById(userId);
        if (user == null || user.getRoleId() != 1) {
            return Result.error("仅管理员可查看所有反馈");
        }
        return Result.success(feedbackService.findPage(page, pageSize));
    }

    @GetMapping("/my")
    public Result<List<Feedback>> my() {
        Integer userId = CurrentUserUtils.getCurrentUserId();
        return Result.success(feedbackService.findByUserId(userId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Feedback feedback) {
        Integer userId = CurrentUserUtils.getCurrentUserId();
        User user = userService.findById(userId);
        if (user == null) return Result.error("用户不存在");
        feedback.setUserId(userId);
        feedback.setUserName(user.getUserName());
        feedbackService.add(feedback);
        return Result.success("提交成功");
    }

    @PostMapping("/reply")
    public Result<Void> reply(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        String replyContent = (String) params.get("replyContent");
        Integer userId = CurrentUserUtils.getCurrentUserId();
        User user = userService.findById(userId);
        if (user == null || user.getRoleId() != 1) {
            return Result.error("仅管理员可回复");
        }
        Feedback fb = feedbackService.findById(id);
        if (fb == null) {
            return Result.error("反馈记录不存在");
        }

        feedbackService.reply(id, replyContent, userId, user.getUserName());

        Message msg = new Message();
        msg.setFromUserId(userId);
        msg.setFromUserName(user.getUserName());
        msg.setToUserId(fb.getUserId());
        msg.setTitle("反馈回复通知");
        msg.setContent(replyContent);
        msg.setMsgType("feedback_reply");
        msg.setRelatedId(id);
        messageService.send(msg);

        return Result.success("回复成功");
    }

    private static final String UPLOAD_DIR = "uploads/feedback/";

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            if (originalName == null) originalName = "file";
            String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID() + ext;
            Path dir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename));
            return Result.success("/uploads/feedback/" + filename);
        } catch (Exception e) {
            return Result.error("上传失败");
        }
    }
}
