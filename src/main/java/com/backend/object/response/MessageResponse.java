package com.backend.object.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private String type;
}
