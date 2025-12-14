package io.github.hypecycle.chatpipeline.connector.chzzk;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hypecycle.chatpipeline.connector.chzzk.dto.response.ChzzkResponseMessage;
import io.github.hypecycle.chatpipeline.domain.ChatMessage;
import io.github.hypecycle.chatpipeline.domain.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChzzkMessageMapperTest {

    private ChzzkMessageMapper messageMapper;

    @BeforeEach
    void setUp() {
        messageMapper = new ChzzkMessageMapper(new ObjectMapper());
    }

    @Test
    void 일반_채팅_메시지가_오면_정상적으로_파싱된다() {
        // given
        String profileJson = """
            {
                "userIdHash": "user123",
                "nickname": "테스터"
            }
            """;
        String extrasJson = """
            {
                "osType": "WINDOWS",
                "chatType": "STREAMING"
            }
            """;
        ChzzkResponseMessage.Body body = new ChzzkResponseMessage.Body(
                profileJson,
                extrasJson,
                "안녕하세요", // 일반 메시지 타입
                1,
                1672531200000L  // 2023-01-01 00:00:00
        );

        // when
        ChatMessage result = messageMapper.parse(body);

        // then
        assertThat(result.platform()).isEqualTo(io.github.hypecycle.chatpipeline.domain.Platform.CHZZK);
        assertThat(result.messageType()).isEqualTo(MessageType.NORMAL);
        assertThat(result.author().id()).isEqualTo("user123");
        assertThat(result.author().nickname()).isEqualTo("테스터");
        assertThat(result.message()).isEqualTo("안녕하세요");
        assertThat(result.time().getYear()).isEqualTo(2023);
        assertThat(result.headers()).containsEntry("osType", "WINDOWS");
    }

    @Test
    void 도네이션_메시지가_오면_도네이션_정보가_추가적으로_파싱된다() {
        // given
        String profileJson = """
            {
                "userIdHash": "donator456",
                "nickname": "후원자"
            }
            """;
        String extrasJson = """
            {
                "osType": "AOS",
                "chatType": "STREAMING",
                "payAmount": 10000,
                "isAnonymous": false
            }
            """;
        ChzzkResponseMessage.Body body = new ChzzkResponseMessage.Body(
            profileJson,
            extrasJson,
            "잘 보고 있어요!",
            10, // 도네이션 메시지 타입
            1672542000000L // 2023-01-01 03:00:00
        );

        // when
        ChatMessage result = messageMapper.parse(body);

        // then
        assertThat(result.messageType()).isEqualTo(MessageType.DONATION);
        assertThat(result.author().nickname()).isEqualTo("후원자");
        assertThat(result.message()).isEqualTo("잘 보고 있어요!");
        assertThat(result.headers()).containsEntry("payAmount", 10000L);
        assertThat(result.headers()).containsEntry("isAnonymous", false);
    }
}
