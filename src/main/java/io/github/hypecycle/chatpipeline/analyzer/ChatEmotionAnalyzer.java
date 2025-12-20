package io.github.hypecycle.chatpipeline.analyzer;

import io.github.hypecycle.chatpipeline.domain.ChatMessage;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ChatEmotionAnalyzer {

    @Async("chatWorkerThreadPoolTaskExecutor")
    public void analyze(List<ChatMessage> buffer) {
        // llm과 연계하여 본격적인 감정분석 구현
    }
}
