package org.aigor.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("visualization");
        registry.addViewController("/visualization").setViewName("visualization");
        registry.addViewController("/app-status").setViewName("app-status");
        registry.addViewController("/login").setViewName("login");
    }
}
