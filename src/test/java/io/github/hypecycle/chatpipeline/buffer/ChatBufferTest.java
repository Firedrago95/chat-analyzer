package io.github.hypecycle.chatpipeline.buffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.github.hypecycle.chatpipeline.domain.ChatMessage;
import io.github.hypecycle.chatpipeline.domain.MessageType;
import io.github.hypecycle.chatpipeline.domain.Platform;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatBufferTest {

    private ChatBuffer chatBuffer;

    @BeforeEach
    void setup() {
        chatBuffer = new ChatBuffer();
    }

    @Test
    void produce_shouldMaintainOrder() throws InterruptedException {
        // given
        LocalDateTime now = LocalDateTime.now();
        ChatMessage chat1 = new ChatMessage(Platform.CHZZK, MessageType.NORMAL, null, "1", now, null);
        ChatMessage chat2 = new ChatMessage(Platform.CHZZK, MessageType.NORMAL, null, "2", now, null);
        ChatMessage chat3 = new ChatMessage(Platform.CHZZK, MessageType.NORMAL, null, "3", now, null);

        chatBuffer.produce(chat1);
        chatBuffer.produce(chat2);
        chatBuffer.produce(chat3);

        // when
        List<ChatMessage> chatMessages = chatBuffer.drainBatch(3, 2000);

        // then
        assertAll(
            () -> assertThat(chatMessages.size()).isEqualTo(3),
            () -> assertThat(chatMessages.get(0).message()).isEqualTo("1"),
            () -> assertThat(chatMessages.get(2).message()).isEqualTo("3")
        );
    }
}
