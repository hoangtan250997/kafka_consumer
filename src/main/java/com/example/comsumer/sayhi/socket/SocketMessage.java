package com.example.comsumer.sayhi.socket;

import lombok.Getter;

@Getter
public class SocketMessage {
    private String message;
    public SocketMessage() {
    }

    public SocketMessage(String message) {
        this.message = message;
    }
}
