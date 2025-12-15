package io.github.hypecycle.chatpipeline.connector.chzzk;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.hypecycle.chatpipeline.global.ChzzkPipelineException;
import io.github.hypecycle.chatpipeline.global.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChzzkChatConnectServiceTest {

    @InjectMocks
    private ChzzkChatConnectService connectService;

    @Mock
    private ChannelIdReader channelIdReader;

    @Mock
    private ChzzkApiClient chzzkApiClient;

    @Mock
    private ChzzkWebsocketClient websocketClient;

    @Mock
    private ChzzkWebsocketClientFactory chzzkWebsocketClientFactory;

    @Test
    void 정상적인_경우_웹소켓_연결에_성공한다() throws Exception {
        // given
        given(channelIdReader.readChannelId()).willReturn("channel1");
        given(chzzkApiClient.getChatChannelId("channel1")).willReturn("chatChannel1");
        given(chzzkApiClient.getAccessToken("chatChannel1")).willReturn("token1");
        given(chzzkWebsocketClientFactory.create("chatChannel1", "token1"))
                .willReturn(websocketClient);

        // when
        connectService.run();

        // Then
        verify(channelIdReader).readChannelId();
        verify(chzzkApiClient).getChatChannelId("channel1");
        verify(chzzkApiClient).getAccessToken("chatChannel1");
        verify(websocketClient).connectBlocking();
    }

    @Test
    void 연결_실패_시_2초_후_재시도한다() throws Exception {
        // given
        given(channelIdReader.readChannelId())
            .willReturn("channel1");
        given(chzzkApiClient.getChatChannelId("channel1"))
            .willThrow(new ChzzkPipelineException(ErrorCode.INVALID_CHANNEL_ID))
            .willReturn("chatChannel1"); // 첫 호출은 실패, 두 번째는 성공
        given(chzzkApiClient.getAccessToken("chatChannel1"))
            .willReturn("token1");
        given(chzzkWebsocketClientFactory.create("chatChannel1", "token1"))
            .willReturn(websocketClient);

        // when
        doThrow(new RuntimeException("Test End"))
                .when(websocketClient).connectBlocking();

        assertThatThrownBy(() -> connectService.run())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Test End");

        // then
        verify(chzzkApiClient, times(2)).getChatChannelId("channel1");
        verify(websocketClient).connectBlocking(); // 최종적으로 연결 성공
    }
}
