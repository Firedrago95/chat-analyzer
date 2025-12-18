package io.github.hypecycle.chatpipeline.analyzer;

import io.github.hypecycle.chatpipeline.buffer.ChatBuffer;
import io.github.hypecycle.chatpipeline.domain.ChatMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ChatAnalyzeService {

    private ChatBuffer chatBuffer;
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Async
    public void analyze() throws InterruptedException {
        while (!Thread.interrupted()) {
            ChatMessage chatMessage = chatBuffer.take();

            threadPoolTaskExecutor.execute(() -> {});
        }
    }
}
