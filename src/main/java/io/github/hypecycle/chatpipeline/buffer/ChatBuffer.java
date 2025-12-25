package io.github.hypecycle.chatpipeline.buffer;

import io.github.hypecycle.chatpipeline.domain.ChatMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatBuffer {

    private final BlockingDeque<ChatMessage> queue = new LinkedBlockingDeque<>();

    public void produce(ChatMessage chatMessage) {
        queue.offer(chatMessage);
    }

    public List<ChatMessage> drainBatch(int minSize, int maxSize, long timeoutMs) throws InterruptedException {
        List<ChatMessage> tempBatch = new ArrayList<>();

        // 첫 번째 메시지는 올 때까지 무한 대기
        tempBatch.add(queue.take());

        long deadLine = System.currentTimeMillis() + timeoutMs;

        // 최소 개수가 채워질 때까지 '남은 시간' 동안 계속 대기
        while (tempBatch.size() < minSize) {
            long remaining = deadLine - System.currentTimeMillis();
            if (remaining <= 0) break; // 전체 타임아웃 종료 시 탈출

            // '남은 전체 시간'만큼 기다리거나 중간에 데이터가 들어오면 바로 깨어남
            ChatMessage next = queue.poll(remaining, TimeUnit.MILLISECONDS);

            if (next != null) {
                tempBatch.add(next);
            } else {
                // poll이 null을 줬다는 건 진짜로 remaining 시간을 다 썼다는 뜻
                break;
            }
        }

        // 최소 개수를 채웠는데 큐에 더 있다면 최대 개수(maxSize)까지 싹 긁어옴
        if (tempBatch.size() < maxSize && !queue.isEmpty()) {
            queue.drainTo(tempBatch, maxSize - tempBatch.size());
        }

        log.info("[----] 버퍼처리 개수: {},  현재 버퍼 대기 : {}", tempBatch.size(),queue.size());

        return List.copyOf(tempBatch);
    }
}
