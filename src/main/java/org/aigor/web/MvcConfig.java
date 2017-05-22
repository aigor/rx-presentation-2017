package org.aigor.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("visualization");
        registry.addViewController("/visualization").setViewName("visualization");
        registry.addViewController("/app-status").setViewName("app-status");
        registry.addViewController("/login").setViewName("login");
    }
}
