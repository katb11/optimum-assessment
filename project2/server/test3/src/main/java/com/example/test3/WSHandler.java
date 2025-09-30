package com.example.test3;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WSHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messagePayload = message.getPayload();
        session.sendMessage(new TextMessage("The server received your message \"" + messagePayload + "\""));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("New WebSocket connection: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("WebSocket connection closed: " + session.getId() + " - " + status.getReason());
    }
}