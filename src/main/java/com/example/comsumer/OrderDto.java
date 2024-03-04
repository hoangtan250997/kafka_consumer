package com.example.comsumer;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class OrderDto {
String item;
Double amount;
}
