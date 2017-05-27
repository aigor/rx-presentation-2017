/**
 * Copyright (C) Zoomdata, Inc. 2012-2017. All rights reserved.
 */
package org.aigor.web.ws;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.StandardWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import java.net.URI;
import java.time.Duration;

import static org.junit.Assert.assertEquals;

public class WebSocketClientTest {

    private StandardWebSocketClient client;

    @Before
    public void setUp() throws Exception {
        client = new StandardWebSocketClient();
    }

    @Test
    public void shouldCommunicateWithWs() throws Exception {
        int count = 1;
        Flux<String> input = Flux.range(1, count).map(index -> "msg-" + index);
        ReplayProcessor<Object> output = ReplayProcessor.create(count);

        client.execute(new URI("ws://localhost:8080/ws/vis"),
                session -> session
                        .send(input.map(session::textMessage))
                        .thenMany(session.receive().take(count).map(WebSocketMessage::getPayloadAsText))
                        .subscribeWith(output)
                        .then())
                .block(Duration.ofMillis(5000));

        assertEquals(input.collectList().block(Duration.ofMillis(5000)),
                output.collectList().block(Duration.ofMillis(5000)));
    }
}
