package com.plexpt.chatgpt;

import com.alibaba.fastjson2.util.UUIDUtils;
import com.plexpt.chatgpt.entity.billing.CreditGrantsResponse;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.util.ChatContextHolder;
import com.plexpt.chatgpt.util.Proxys;

import org.junit.Before;

import java.net.Proxy;
import java.util.Arrays;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Test {
    public static void main(String[] args) {

        System.out.println("test");
    }

    private ChatGPT chatGPT;

    @Before
    public void before() {
        // Proxy proxy = Proxys.http("127.0.0.1", 1080);

        chatGPT = ChatGPT.builder()
                .apiKey("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")
                .timeout(900)
                // .proxy(proxy)
                .apiHost("https://api.openai.com/") //代理地址
                .build()
                .init();

        CreditGrantsResponse response = chatGPT.creditGrants();
        log.info("余额：{}", response.getTotalAvailable());
    }

    @org.junit.Test
    public void chat() {
        Message system = Message.ofSystem("你现在是一个诗人，专门写七言绝句");
        Message message = Message.of("写一段七言绝句诗，题目是：火锅！");

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .messages(Arrays.asList(system, message))
                .maxTokens(3000)
                .temperature(0.9)
                .build();
        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        Message res = response.getChoices().get(0).getMessage();
        System.out.println(res);
    }

    @org.junit.Test
    public void chatmsg() {
        String res = chatGPT.chat("写一段七言绝句诗，题目是：火锅！");
        System.out.println(res);
    }


    @org.junit.Test
    public void chatContext() {
        String chatId = UUID.randomUUID().toString();

        Message m1 = Message.of("安静的近义词有哪些？");
        ChatContextHolder.add(chatId, m1);

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .messages(ChatContextHolder.get(chatId))
                .maxTokens(3000)
                .temperature(0.9)
                .build();
        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        Message res = response.getChoices().get(0).getMessage();
        System.out.println(res);

        // 接着问
        Message m2 = Message.of("列出5个即可？");
        ChatContextHolder.add(chatId, m2);
        chatCompletion.setMessages(ChatContextHolder.get(chatId));
        response = chatGPT.chatCompletion(chatCompletion);
        res = response.getChoices().get(0).getMessage();
        System.out.println(res);

    }
}
