package com.backend.config.socket;

import com.backend.object.response.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class SocketManager {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, SocketIOClient> ioClientMap = new HashMap<>();

    @PostConstruct
    public void inits() {
        System.out.println("inits..............");
        Configuration config = new Configuration();
        config.setHostname("127.0.0.1");
        config.setPort(9999);
        SocketIOServer socketIOServer = new SocketIOServer(config);
        socketIOServer.addConnectListener(
                socketIOClient -> System.out.println("onConnect Test connect.........."));
        socketIOServer.addDisconnectListener(socketIOClient -> {
            System.out.println("onDisconnect Test connect..........");
            for (String s : ioClientMap.keySet()) {
                if (ioClientMap.get(s) == socketIOClient) {
                    ioClientMap.remove(s);
                }
            }
        });
        socketIOServer.addEventListener("connected", String.class,
                (socketIOClient, s, ackRequest) -> {
                    System.out.println("onData Test connect.........." + s);
                    ioClientMap.put(s, socketIOClient);
                });

        socketIOServer.addEventListener("message", String.class,
                (socketIOClient, s, ackRequest) -> {
                    System.out.println("onData Test connect.........." + s);

                    MessageResponse message =
                            objectMapper.readValue(s, MessageResponse.class);
                    int receiverId = message.getReceiverId();
                    if (ioClientMap.containsKey(receiverId+"")){
                        ioClientMap.get(receiverId+"").sendEvent("message", s);
                    }
                    saveMessage(message);
                });
        socketIOServer.start();
    }

    private void saveMessage(MessageResponse msg){
    }
}
