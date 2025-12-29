package com.smartscheduleassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testChatSingleMessage() throws Exception {
        Map<String, Object> request = new HashMap<>();
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "你好");
        messages.add(message);
        request.put("messages", messages);

        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").exists());
    }

    @Test
    void testChatMultipleMessages() throws Exception {
        Map<String, Object> request = new HashMap<>();
        List<Map<String, String>> messages = new ArrayList<>();
        
        Map<String, String> msg1 = new HashMap<>();
        msg1.put("role", "user");
        msg1.put("content", "你好");
        messages.add(msg1);
        
        Map<String, String> msg2 = new HashMap<>();
        msg2.put("role", "assistant");
        msg2.put("content", "你好，我是你的智能助手。");
        messages.add(msg2);
        
        Map<String, String> msg3 = new HashMap<>();
        msg3.put("role", "user");
        msg3.put("content", "帮我规划一下学习任务");
        messages.add(msg3);
        
        request.put("messages", messages);

        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").exists());
    }
}

