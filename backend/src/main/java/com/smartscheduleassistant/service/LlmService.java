package com.smartscheduleassistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LlmService {
    private static final Logger logger = LoggerFactory.getLogger(LlmService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.provider:deepseek}")  // 可选: openai, deepseek, claude, wenxin, qwen等
    private String aiProvider;

    @Value("${AI_API_KEY:${ai.api.key:}}")  // 优先从环境变量读取，否则从配置文件读取
    private String apiKey;

    @Value("${AI_API_URL:${ai.api.url:}}")  // 优先从环境变量读取，否则从配置文件读取
    private String apiUrl;

    /**
     * AI分类任务
     * 根据任务内容调用LLM进行分类
     */
    public String classify(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "其他";
        }

        // 构造分类Prompt
        String prompt = String.format(
            "请为下面的任务进行分类，只能返回以下分类之一：学习、工作、健康、生活、其他。\n" +
            "只返回分类名称，不要返回其他内容。\n" +
            "任务：%s",
            content
        );

        try {
            String response = callLlmApi(prompt);
            // 提取分类结果（去除可能的标点符号和空格）
            String category = extractCategory(response);
            return validateCategory(category);
        } catch (Exception e) {
            logger.warn("AI分类调用失败，使用fallback: {}", e.getMessage());
            // 如果AI调用失败，使用简单的关键词匹配作为fallback
            return fallbackClassify(content);
        }
    }

    /**
     * AI聊天回复
     * 接收完整的对话历史，返回AI回复
     */
    public String chatReply(List<?> messages) {
        if (messages == null || messages.isEmpty()) {
            return "你好，我是智能日程助手，有什么可以帮助你的吗？";
        }

        try {
            // 将messages转换为LLM API需要的格式
            String response = callLlmChatApi(messages);
            return response;
        } catch (Exception e) {
            logger.error("AI聊天调用失败: {}", e.getMessage(), e);
            return "抱歉，我现在无法回复，请稍后再试。错误信息: " + e.getMessage();
        }
    }

    /**
     * 调用LLM API进行分类
     */
    private String callLlmApi(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("AI API Key未配置，请在application.yml中设置ai.api.key");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);

        // 根据不同的AI提供商构造不同的请求体
        Map<String, Object> requestBody = buildClassificationRequest(prompt);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        String url = apiUrl != null && !apiUrl.isEmpty() 
            ? apiUrl 
            : getDefaultApiUrl();

        ResponseEntity<String> response = restTemplate.exchange(
            url, 
            HttpMethod.POST, 
            request, 
            String.class
        );

        return parseLlmResponse(response.getBody());
    }

    /**
     * 调用LLM API进行聊天
     */
    private String callLlmChatApi(List<?> messages) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("AI API Key未配置，请在application.yml中设置ai.api.key");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = buildChatRequest(messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        String url = apiUrl != null && !apiUrl.isEmpty() 
            ? apiUrl 
            : getDefaultChatUrl();

        ResponseEntity<String> response = restTemplate.exchange(
            url, 
            HttpMethod.POST, 
            request, 
            String.class
        );

        return parseLlmResponse(response.getBody());
    }

    /**
     * 构造分类请求体（OpenAI兼容格式）
     */
    private Map<String, Object> buildClassificationRequest(String prompt) {
        return Map.of(
            "model", getModelName(),
            "messages", List.of(
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.3,
            "max_tokens", 10
        );
    }

    /**
     * 构造聊天请求体（OpenAI兼容格式）
     */
    private Map<String, Object> buildChatRequest(List<?> messages) {
        // 确保 messages 格式正确，并记录日志
        if (messages != null && !messages.isEmpty()) {
            Object firstMsg = messages.get(0);
            if (firstMsg instanceof Map<?, ?>) {
                Map<?, ?> firstMsgMap = (Map<?, ?>) firstMsg;
                Object role = firstMsgMap.get("role");
                logger.info("构建聊天请求，第一条消息role: {}, 总消息数: {}", role, messages.size());
            }
        }
        
        return Map.of(
            "model", getModelName(),
            "messages", messages,
            "temperature", 0.7,
            "max_tokens", 500
        );
    }

    /**
     * 根据provider获取模型名称
     */
    private String getModelName() {
        if (apiUrl != null && !apiUrl.isEmpty()) {
            // 如果指定了自定义URL，默认使用deepseek-chat（兼容OpenAI格式）
            return "deepseek-chat";
        }
        switch (aiProvider.toLowerCase()) {
            case "deepseek":
                return "deepseek-chat";
            case "openai":
                return "gpt-3.5-turbo";
            default:
                return "deepseek-chat"; // 默认使用DeepSeek
        }
    }

    /**
     * 解析LLM响应（OpenAI格式）
     */
    private String parseLlmResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText()
                .trim();
        } catch (Exception e) {
            throw new RuntimeException("解析AI响应失败: " + e.getMessage());
        }
    }

    /**
     * 提取分类结果
     */
    private String extractCategory(String response) {
        String[] categories = {"学习", "工作", "健康", "生活", "其他"};
        String lowerResponse = response.toLowerCase();
        
        for (String cat : categories) {
            if (lowerResponse.contains(cat.toLowerCase())) {
                return cat;
            }
        }
        return "其他";
    }

    /**
     * 验证分类结果
     */
    private String validateCategory(String category) {
        String[] validCategories = {"学习", "工作", "健康", "生活", "其他"};
        for (String valid : validCategories) {
            if (valid.equals(category)) {
                return category;
            }
        }
        return "其他";
    }

    /**
     * Fallback分类（当AI调用失败时使用）
     */
    private String fallbackClassify(String content) {
        if (content == null) return "其他";
        String c = content.toLowerCase();
        if (c.contains("学习") || c.contains("课程") || c.contains("作业") || c.contains("考试")) return "学习";
        if (c.contains("会议") || c.contains("开会") || c.contains("工作") || c.contains("项目")) return "工作";
        if (c.contains("锻炼") || c.contains("运动") || c.contains("跑步") || c.contains("健身") || c.contains("健康")) return "健康";
        if (c.contains("吃饭") || c.contains("购物") || c.contains("娱乐") || c.contains("休息")) return "生活";
        return "其他";
    }

    /**
     * 获取默认API URL
     */
    private String getDefaultApiUrl() {
        if (apiUrl != null && !apiUrl.isEmpty()) {
            return apiUrl;
        }
        switch (aiProvider.toLowerCase()) {
            case "deepseek":
                return "https://api.deepseek.com/v1/chat/completions";
            case "openai":
                return "https://api.openai.com/v1/chat/completions";
            default:
                return "https://api.deepseek.com/v1/chat/completions"; // 默认使用DeepSeek
        }
    }

    /**
     * 获取默认聊天API URL
     */
    private String getDefaultChatUrl() {
        return getDefaultApiUrl(); // 分类和聊天使用同一个端点
    }
}
