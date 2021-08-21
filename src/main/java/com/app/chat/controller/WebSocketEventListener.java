package com.app.chat.controller;

import com.app.chat.model.ChatMessage;
import com.app.chat.model.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Session 연결 이벤트 처리
     * 
     * @param event
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    /**
     * Session 종료 이벤트 처리
     * 
     * @param event
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = getUserNameFromEvent(event);

        if (username != null) {
            log.info("User disconnected : {}", username);
            sendDisconnectMessage(username);
        }
    }

    private String getUserNameFromEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        return username;
    }

    private void sendDisconnectMessage(String username) {
        ChatMessage chatMessage = createChatMessage(username, MessageType.LEAVE);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    private ChatMessage createChatMessage(String username, MessageType messageType) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(messageType);
        chatMessage.setSender(username);
        return chatMessage;
    }

}
