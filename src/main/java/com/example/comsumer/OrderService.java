package com.example.comsumer;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository foodOrderRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(OrderRepository foodOrderRepository, ModelMapper modelMapper) {
        this.foodOrderRepository = foodOrderRepository;
        this.modelMapper = modelMapper;
    }

    public void persistFoodOrder(OrderDto foodOrderDto) {
        ItemOrder foodOrder = modelMapper.map(foodOrderDto, ItemOrder.class);
        ItemOrder persistedFoodOrder = foodOrderRepository.save(foodOrder);
    }
}
