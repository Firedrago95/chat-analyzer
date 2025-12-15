package io.github.hypecycle.chatpipeline.connector.chzzk.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

@Slf4j
public class ChzzkWebsocketClient extends WebSocketClient {

    private final String chatChannelId;
    private final String accessToken;
    private final ChzzkMessageHandler messageHandler;

    private ScheduledExecutorService pingScheduler;

    public ChzzkWebsocketClient(
        ChzzkMessageHandler messageHandler,
        String chatChannelId,
        String accessToken
    ) throws URISyntaxException {
        super(new URI("wss://kr-ss1.chat.naver.com/chat"));
        this.messageHandler = messageHandler;
        this.chatChannelId = chatChannelId;
        this.accessToken = accessToken;
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        this.pingScheduler = Executors.newSingleThreadScheduledExecutor();
        messageHandler.handleOpen(this, chatChannelId, accessToken, pingScheduler);
    }

    @Override
    public void onMessage(String message) {
        messageHandler.handleMessage(message)
            .ifPresent(this::send);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        messageHandler.handleClose(reason, pingScheduler);
    }

    @Override
    public void onError(Exception ex) {
        messageHandler.handleError(ex);
    }
}
