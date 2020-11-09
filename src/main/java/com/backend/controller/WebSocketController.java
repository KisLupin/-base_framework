package com.backend.controller;

import com.backend.domain.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/javainuse")
    public Message sendMessage(@Payload Message webSocketChatMessage) {
        return webSocketChatMessage;
    }
    @MessageMapping("/chat.newUser")
    @SendTo("/topic/javainuse")
    public Message newUser(@Payload Message webSocketChatMessage,
                                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getText());
        return webSocketChatMessage;
    }

}
