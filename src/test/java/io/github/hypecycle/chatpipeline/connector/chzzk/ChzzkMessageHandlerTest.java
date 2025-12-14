package io.github.hypecycle.chatpipeline.connector.chzzk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hypecycle.chatpipeline.connector.chzzk.dto.response.ChzzkCommand;
import io.github.hypecycle.chatpipeline.connector.chzzk.dto.response.ChzzkResponseMessage;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChzzkMessageHandlerTest {

    private ChzzkMessageHandler messageHandler;

    @Mock
    private ChzzkMessageMapper messageMapper;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        messageHandler = new ChzzkMessageHandler(messageMapper, objectMapper);
    }

    @Test
    void PING_메시지를_받으면_PONG_메시지를_반환한다() throws Exception {
        // given
        String pingMessage = "{\"cmd\":0}";
        ChzzkResponseMessage pingResponse = new ChzzkResponseMessage(
            ChzzkCommand.PING, null, null, null);
        when(objectMapper.readValue(pingMessage, ChzzkResponseMessage.class)).thenReturn(
            pingResponse);

        // when
        Optional<String> result = messageHandler.handleMessage(pingMessage);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).contains("\"cmd\": 10000");
    }

    @Test
    void CHAT_메시지를_받으면_메시지_파서를_호출한다() throws Exception {
        // given
        String chatMessageJson = """
            {
              "cmd": 1,
              "bdy": [
                {
                  "msg": "test message",
                  "profile": "{}",
                  "msgTypeCode": 1,
                  "msgTime": 1234567890,
                  "extras": "{}"
                }
              ]
            }
            """;
        ChzzkResponseMessage.Body[] body = {
            new ChzzkResponseMessage.Body("{}", "{}", "text message", 1, 1234567890)};
        ChzzkResponseMessage chatResponse = new ChzzkResponseMessage(
            ChzzkCommand.CHAT, "1","game", List.of(body));
        when(objectMapper.readValue(anyString(), any(Class.class))).thenReturn(chatResponse);

        // when
        messageHandler.handleMessage(chatMessageJson);

        // then
        verify(messageMapper).parse(any(ChzzkResponseMessage.Body.class));
    }
}
