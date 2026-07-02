package com.example.changshademo.controller;

import com.example.changshademo.common.CurrentUserUtils;
import com.example.changshademo.common.PageResult;
import com.example.changshademo.common.Result;
import com.example.changshademo.entity.Message;
import com.example.changshademo.entity.User;
import com.example.changshademo.service.MessageService;
import com.example.changshademo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/list")
    public Result<PageResult<Message>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Integer userId = CurrentUserUtils.getCurrentUserId();
        return Result.success(messageService.findByUserId(userId, page, pageSize));
    }

    @GetMapping("/unread")
    public Result<Long> unread() {
        Integer userId = CurrentUserUtils.getCurrentUserId();
        return Result.success(messageService.countUnread(userId));
    }

    @PostMapping("/read/{id}")
    public Result<Void> markRead(@PathVariable Integer id) {
        messageService.markAsRead(id);
        return Result.success("已读");
    }

    @PostMapping("/send")
    public Result<Void> send(@RequestBody Message message) {
        Integer userId = CurrentUserUtils.getCurrentUserId();
        User user = userService.findById(userId);
        if (user == null) return Result.error("用户不存在");
        message.setFromUserId(userId);
        message.setFromUserName(user.getUserName());
        messageService.send(message);
        return Result.success("发送成功");
    }
}
