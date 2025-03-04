package com.reportweaver.reportweaver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.reportweaver.reportweaver.websocket.SeleniumStatusHandler;

/**
 * Configuration class for WebSocket support in the application.
 * Enables WebSocket communication and registers WebSocket handlers.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * Registers WebSocket handlers for handling real-time communication.
     *
     * @param registry The WebSocketHandlerRegistry to register handlers.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SeleniumStatusHandler(), "/ws/selenium-status")
                .setAllowedOrigins("*"); // Allow all origins (Change this for production)
    }

    /**
     * Creates and exposes a SeleniumStatusHandler bean.
     * This handler is responsible for processing WebSocket messages related to
     * Selenium status updates.
     *
     * @return An instance of SeleniumStatusHandler.
     */
    @Bean
    public SeleniumStatusHandler seleniumStatusHandler() {
        return new SeleniumStatusHandler();
    }
}
