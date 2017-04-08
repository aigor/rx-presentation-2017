package org.aigor.web.rest;

import org.aigor.web.dao.IVisualizationRepository;
import org.aigor.web.dao.Visualization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller to work with available visualizations.
 */
@RestController
@RequestMapping("/api/visualizations")
public class VisualizationController {

    private final IVisualizationRepository visRepo;

    public VisualizationController(IVisualizationRepository visRepo) {
        this.visRepo = visRepo;
    }

    @GetMapping("")
    public Flux<Visualization> list(){
        return visRepo.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Visualization> visById(@PathVariable String id){
        return visRepo.findById(id);
    }
}
