package org.aigor.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.core.publisher.Mono;

import java.util.Locale;


//@Configuration
public class WebViewConfig implements WebFluxConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
//        registry.addViewController("/").setViewName("visualization");
//        registry.addViewController("/visualization").setViewName("visualization");
//        registry.addViewController("/app-status").setViewName("app-status");
//        registry.addViewController("/login").setViewName("login");

    }

}
