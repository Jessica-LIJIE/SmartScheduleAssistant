package com.smartscheduleassistant.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LlmServiceTest {

    @Autowired
    private LlmService llmService;

    @Test
    void testClassifyLearning() {
        String category = llmService.classify("完成强化学习课程作业");
        assertEquals("学习", category);
    }

    @Test
    void testClassifyWork() {
        String category = llmService.classify("参加项目会议");
        assertEquals("工作", category);
    }

    @Test
    void testClassifyHealth() {
        String category = llmService.classify("去健身房锻炼");
        assertEquals("健康", category);
    }

    @Test
    void testClassifyOther() {
        String category = llmService.classify("看电影");
        assertEquals("其他", category);
    }

    @Test
    void testClassifyNull() {
        String category = llmService.classify(null);
        assertEquals("其他", category);
    }

    @Test
    void testChatReply() {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "你好");
        messages.add(message);

        String reply = llmService.chatReply(messages);
        assertNotNull(reply);
        assertFalse(reply.isEmpty());
    }
}

