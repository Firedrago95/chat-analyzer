package io.github.hypecycle.chatpipeline.connector.chzzk.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChzzkResponseMessage(
        ChzzkCommand cmd,
        String ver,
        String svcid,
        JsonNode bdy
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(
            String profile,
            String extras,
            String msg,
            int msgTypeCode,
            long msgTime
    ) {}
}
