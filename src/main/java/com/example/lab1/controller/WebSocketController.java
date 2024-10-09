package com.example.lab1.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyClients(String message) {
        // Отправляем сообщение всем клиентам, подписанным на /topic/updates
        messagingTemplate.convertAndSend("/topic/updates", message);
    }
}
