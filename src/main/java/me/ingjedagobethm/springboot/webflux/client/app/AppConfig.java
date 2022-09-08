package me.ingjedagobethm.springboot.webflux.client.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value(value = "${config.base.endpoint}")
    private String url;

    @Bean
    public WebClient webClientRegister(){
        return WebClient.create(url);
    }

}
