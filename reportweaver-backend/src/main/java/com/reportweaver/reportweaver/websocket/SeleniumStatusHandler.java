package com.reportweaver.reportweaver.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket handler for sending real-time Selenium status updates to connected
 * clients.
 */
public class SeleniumStatusHandler extends TextWebSocketHandler {

    // Thread-safe list to store active WebSocket sessions.
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    /**
     * Called when a new WebSocket connection is established.
     * Adds the new session to the list of active WebSocket sessions.
     *
     * @param session The WebSocket session that was established.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    /**
     * Sends a status update message to all active WebSocket clients.
     * Ensures that messages are only sent to open sessions.
     *
     * @param status The status message to send.
     */
    public void sendUpdate(String status) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(status));
                } catch (IOException e) {
                    System.err.println("Failed to send Selenium update: " + e.getMessage());
                }
            }
        }
    }
}
