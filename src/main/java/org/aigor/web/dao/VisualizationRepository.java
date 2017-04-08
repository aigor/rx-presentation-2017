package org.aigor.web.dao;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class VisualizationRepository implements IVisualizationRepository {

    private final List<Visualization> visualizations =
            Arrays.asList(
                    new Visualization("1", "sales-data", "aigor"),
                    new Visualization("2", "reviews-data", "aigor"));

    @Override
    public Flux<Visualization> findAll() {
        return Flux.fromIterable(visualizations);
    }

    @Override
    public Mono<Visualization> findById(String id) {
        Optional<Visualization> searchResult = visualizations.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
        return Mono.justOrEmpty(searchResult);
    }
}
