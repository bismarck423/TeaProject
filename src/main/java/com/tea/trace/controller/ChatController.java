package com.tea.trace.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.trace.entity.TeaMessage;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.mapper.TeaMessageMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private TeaMessageMapper messageMapper;


    @PostMapping("/send")
    public String sendMessage(@RequestBody TeaMessage message, HttpSession session) {
        TeaUser currentUser = (TeaUser) session.getAttribute("user");
        if (currentUser == null) {
            throw new RuntimeException("登录已过期，请重新登录");
        }


        message.setSenderId(currentUser.getId());


        if (!"ADMIN".equals(currentUser.getRole())) {
            message.setReceiverId(1L);
        } else if (message.getReceiverId() == null) {
            throw new RuntimeException("管理员必须指定接收用户");
        }

        message.setCreateTime(LocalDateTime.now());
        message.setIsRead(0);
        messageMapper.insert(message);

        return "SUCCESS";
    }


    @GetMapping("/poll")
    public List<TeaMessage> pollMessages(
            @RequestParam(required = false) Long targetUserId,
            @RequestParam(defaultValue = "0") Long lastMessageId,
            HttpSession session) {

        TeaUser currentUser = (TeaUser) session.getAttribute("user");
        if (currentUser == null) {
            return null;
        }

        Long myId = currentUser.getId();

        // 权限校验与目标锁定
        if (!"ADMIN".equals(currentUser.getRole())) {
            targetUserId = 1L;
        } else if (targetUserId == null) {
            throw new RuntimeException("管理员必须指定要查看的用户");
        }

        LambdaQueryWrapper<TeaMessage> wrapper = new LambdaQueryWrapper<>();


        Long finalTargetUserId = targetUserId;
        wrapper.and(w -> w
                .eq(TeaMessage::getSenderId, myId).eq(TeaMessage::getReceiverId, finalTargetUserId)
                .or()
                .eq(TeaMessage::getSenderId, finalTargetUserId).eq(TeaMessage::getReceiverId, myId)
        );


        if (lastMessageId > 0) {
            wrapper.gt(TeaMessage::getId, lastMessageId);
        }


        wrapper.orderByAsc(TeaMessage::getCreateTime);

        List<TeaMessage> messages = messageMapper.selectList(wrapper);


        if (!messages.isEmpty()) {
            TeaMessage lastMsg = messages.get(messages.size() - 1);
            if (lastMsg.getSenderId().equals(finalTargetUserId) && lastMsg.getIsRead() == 0) {
                TeaMessage updateObj = new TeaMessage();
                updateObj.setIsRead(1);
                LambdaQueryWrapper<TeaMessage> updateWrapper = new LambdaQueryWrapper<>();
                updateWrapper.eq(TeaMessage::getSenderId, finalTargetUserId)
                        .eq(TeaMessage::getReceiverId, myId)
                        .eq(TeaMessage::getIsRead, 0);
                messageMapper.update(updateObj, updateWrapper);
            }
        }

        return messages;
    }
}