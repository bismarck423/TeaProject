package com.tea.trace.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tea.trace.entity.TeaMessage;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.mapper.TeaMessageMapper;
import com.tea.trace.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatHandler.class);

    private static final Map<Long, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    private static final Map<Long, Long> LAST_MESSAGE_TIME = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    private TeaMessageMapper messageMapper;


    @Autowired
    private UserMapper userMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        TeaUser user = (TeaUser) session.getAttributes().get("user");
        if (user != null) {
            SESSIONS.put(user.getId(), session);
            log.info("WebSocket 已连接: 用户ID {}", user.getId());
        } else {
            log.warn("未授权的 WebSocket 连接尝试，强行切断。");
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        TeaUser sender = (TeaUser) session.getAttributes().get("user");
        if (sender == null) {
            return;
        }


        long currentTime = System.currentTimeMillis();
        long lastTime = LAST_MESSAGE_TIME.getOrDefault(sender.getId(), 0L);
        if (currentTime - lastTime < 1000) {
            log.warn("用户ID {} 触发防刷限流，消息已被拦截", sender.getId());

            return;
        }
        LAST_MESSAGE_TIME.put(sender.getId(), currentTime);

        TeaMessage msg = objectMapper.readValue(textMessage.getPayload(), TeaMessage.class);
        msg.setSenderId(sender.getId());


        if (!"ADMIN".equals(sender.getRole())) {

            TeaUser admin = userMapper.selectOne(new LambdaQueryWrapper<TeaUser>()
                    .eq(TeaUser::getRole, "ADMIN")
                    .last("LIMIT 1"));

            if (admin != null) {
                msg.setReceiverId(admin.getId());
            } else {
                log.error("致命异常：系统内未找到 ADMIN 角色的用户，消息无法投递");
                return;
            }
        } else if (msg.getReceiverId() == null) {
            log.error("管理员发送消息丢失 receiverId");
            return;
        }

        msg.setCreateTime(LocalDateTime.now());
        msg.setIsRead(0);

        messageMapper.insert(msg);


        WebSocketSession receiverSession = SESSIONS.get(msg.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
        }

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        TeaUser user = (TeaUser) session.getAttributes().get("user");
        if (user != null) {
            SESSIONS.remove(user.getId());
            LAST_MESSAGE_TIME.remove(user.getId());
            log.info("WebSocket 已断开: 用户ID {}", user.getId());
        }
    }
}