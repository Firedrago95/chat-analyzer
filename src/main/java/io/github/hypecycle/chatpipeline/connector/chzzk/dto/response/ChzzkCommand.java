package io.github.hypecycle.chatpipeline.connector.chzzk.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ChzzkCommand {
    CONNECT_ACK(10100),
    PING(0),
    PONG(10000),
    CHAT(93101),
    DONATION(93102),
    ;

    private final int num;

    ChzzkCommand(int num) {
        this.num = num;
    }

    @JsonValue
    public int getNum() {
        return num;
    }
}
