package io.github.hypecycle.chatpipeline.connector.chzzk;

import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChzzkWebsocketClientFactory {

    private final ChzzkMessageHandler chzzkMessageHandler;

    public ChzzkWebsocketClient create(String chatChannelId, String accessToken) throws URISyntaxException {
        return new ChzzkWebsocketClient(chzzkMessageHandler, chatChannelId, accessToken);
    }
}
