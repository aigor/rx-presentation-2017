package org.aigor.web.dao;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository of visualizations
 */
public interface IVisualizationRepository {
    Flux<Visualization> findAll();
    Mono<Visualization> findById(String id);
}
