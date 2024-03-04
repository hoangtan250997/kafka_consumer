package com.example.comsumer.sayhi.server;


import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
@CrossOrigin
public class ReceptionTvServer {
       private final Set<String> createdThreadSet = new HashSet<>();
    public static final long TIME_DIFFERENCE = 0;

    @Resource
    private final SocketIOServer socketIOServer;


    private long currentTime;

    public ReceptionTvServer(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
        socketIOServer.start();
    }

    @PreDestroy
    public void disconnectSocket() {
        socketIOServer.stop();
    }
}
