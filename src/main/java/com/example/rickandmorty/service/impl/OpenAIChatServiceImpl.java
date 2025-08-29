package com.example.rickandmorty.service.impl;

import com.example.rickandmorty.service.OpenAIChatService;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OpenAIChatServiceImpl implements OpenAIChatService {

    OpenAIClient openAIClient;

    @Override
    public String getChatMessage(String userMessage) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_5_CHAT_LATEST)
                .addUserMessage(userMessage)
                .build();

        ChatCompletion completion = openAIClient.chat().completions().create(params);
        if (completion.choices().getFirst().message().content().isPresent()) {
            return completion.choices().getFirst().message().content().get();
        }
        return "Не выдал ответ";
    }
}
