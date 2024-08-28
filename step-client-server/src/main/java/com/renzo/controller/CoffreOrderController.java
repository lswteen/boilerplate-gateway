package com.renzo.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renzo.controller.model.CoffeeOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class CoffreOrderController {
    private static final String DATA_FILE_PATH = "src/main/resources/input/orders.json";
    private final ObjectMapper objectMapper;

    @GetMapping("/manage-order")
    public ResponseEntity<List<CoffeeOrder>> manageOrder() throws IOException {
        byte[] jsonData = Files.readAllBytes(Paths.get(DATA_FILE_PATH));
        List<CoffeeOrder> coffeeOrders = objectMapper
                .readValue(jsonData, new TypeReference<>(){});
        coffeeOrders.forEach(order -> order.processOrder());
        return ResponseEntity.ok(coffeeOrders);
    }

}
