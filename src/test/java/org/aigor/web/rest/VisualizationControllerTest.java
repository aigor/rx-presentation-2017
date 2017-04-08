package org.aigor.web.rest;

import org.aigor.web.dao.IVisualizationRepository;
import org.aigor.web.dao.Visualization;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Collections.emptyList;

public class VisualizationControllerTest {

    private Visualization vis = new Visualization("1", "2", "3");

    private WebTestClient client;
    @Mock private IVisualizationRepository repository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        client = WebTestClient.bindToController(new VisualizationController(repository))
                .build();
    }

    @Test
    public void shouldListNoVisualizations() throws Exception {
        client.get().uri("api/visualizations")
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class).value().isEqualTo(emptyList());
    }

    @Test
    public void shouldFindVisualization() throws Exception {
        Mockito.when(repository.findById("1"))
                .thenReturn(Mono.just(vis));

        client.get().uri("api/visualizations/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Visualization.class).value().isEqualTo(vis);
    }
}