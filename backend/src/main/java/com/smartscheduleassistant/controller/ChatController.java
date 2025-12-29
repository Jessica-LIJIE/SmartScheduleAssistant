package com.smartscheduleassistant.controller;

import com.smartscheduleassistant.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final LlmService llmService;

    public ChatController(LlmService llmService) { this.llmService = llmService; }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> body) {
        Object msgs = body.get("messages");
        if (msgs instanceof List<?>) {
            List<?> messages = (List<?>) msgs;
            logger.info("收到聊天请求，消息数量: {}", messages.size());
            // 打印第一条消息的内容（应该是 system message 包含任务列表）
            if (!messages.isEmpty()) {
                Object firstMsg = messages.get(0);
                if (firstMsg instanceof Map<?, ?>) {
                    Map<?, ?> firstMsgMap = (Map<?, ?>) firstMsg;
                    Object role = firstMsgMap.get("role");
                    Object content = firstMsgMap.get("content");
                    logger.info("第一条消息 - role: {}, content长度: {}", 
                        role, 
                        content != null ? content.toString().length() : 0);
                    if ("system".equals(role) && content != null) {
                        String contentStr = content.toString();
                        logger.info("System消息内容（前200字符）: {}", 
                            contentStr.length() > 200 ? contentStr.substring(0, 200) : contentStr);
                    }
                }
            }
        } else {
            logger.warn("收到聊天请求，但消息格式异常: {}", msgs);
        }
        String reply = llmService.chatReply((List<?>)msgs);
        return ResponseEntity.ok(Map.of("reply", reply));
    }
}
