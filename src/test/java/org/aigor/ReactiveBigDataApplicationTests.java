package org.aigor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class ReactiveBigDataApplicationTests {

	@Test
    @Ignore
	public void contextLoads() throws URISyntaxException {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        client
                .execute(new URI("ws://localhost:8080/vis"), session -> {
                    log.info("Session");
                    session.send(s -> session.textMessage("Hello"));
                    return session.send(session.receive().doOnNext(WebSocketMessage::retain));
                })
        .block(Duration.ofSeconds(5));
	}

}
