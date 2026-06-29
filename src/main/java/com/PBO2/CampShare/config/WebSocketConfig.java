package com.PBO2.CampShare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Konfigurasi WebSocket (STOMP) untuk fitur chat real-time.
 *
 * - Endpoint koneksi: /ws
 * - Prefix untuk topic yang di-broadcast ke client: /topic
 *   (client subscribe ke /topic/chat/{conversationId})
 * - Prefix untuk pesan yang dikirim client ke server (tidak dipakai
 *   secara aktif di setup ini karena kirim pesan tetap lewat REST
 *   /api/chat/send, tapi disiapkan untuk pengembangan selanjutnya,
 *   misal fitur "sedang mengetik...")
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Broker sederhana bawaan Spring (cukup untuk skala kecil-menengah).
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Sesuaikan dengan origin frontend kamu di production
                .withSockJS(); // Fallback otomatis jika browser/proxy tidak support WebSocket murni
    }
}
