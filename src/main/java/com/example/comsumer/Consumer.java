package com.example.comsumer;

import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Consumer {
    private static final String orderTopic1 = "${topic.name.food}";
    private static final String orderTopic2 = "${topic.name.drink}";

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    private final SocketIOServer socketIOServer;

    public Consumer(ObjectMapper objectMapper, OrderService foodOrderService, SocketIOServer socketIOServer) {
        this.objectMapper = objectMapper;
        this.orderService = foodOrderService;
        this.socketIOServer = socketIOServer;
    }

    @KafkaListener(topics = {orderTopic1} ,groupId = "food")
    public void consumeMessage(String message) throws JsonProcessingException {
        System.out.println(message);
        OrderDto orderDto = objectMapper.readValue(message, OrderDto.class);
        socketIOServer.getBroadcastOperations().sendEvent("food",orderDto);
        orderService.persistFoodOrder(orderDto);
    }

}
