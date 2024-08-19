package com.renzo.controller.model;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeOrder {
    private List<OrderItem> coffeeOrders;
    private String status;
    private Optional<Integer> orderTotalPrice; // Optional 필드
    private Optional<Integer> estimatedWaitTime; // Optional 필드

    private static final Map<String, Integer> coffeePrices = new HashMap<>();
    private static final Map<String, Integer> coffeeTimes = new HashMap<>();

    static {
        coffeePrices.put("Americano", 3000);
        coffeePrices.put("Latte", 4000);
        coffeePrices.put("Cappuccino", 4500);
        coffeePrices.put("Espresso", 3500);

        coffeeTimes.put("Americano", 3);
        coffeeTimes.put("Latte", 5);
        coffeeTimes.put("Cappuccino", 4);
        coffeeTimes.put("Espresso", 2);
    }

    //tell don't ask
    public void processOrder() {
        if(!"in progress".equals(this.status)){
            return;
        }
        int totalPrice = 0;
        int totalTime = 0;

        for (OrderItem item : coffeeOrders) {
            String coffeeType = item.getType();
            int quantity = item.getQuantity();

            // 총 가격 계산
            totalPrice += coffeePrices.get(coffeeType) * quantity;

            // 총 예상 대기시간 계산 (커피 종류가 2잔 이상일 때, 시간 * 수량 / 2)
            int time = coffeeTimes.get(coffeeType) * quantity;
            if (quantity > 1) {
                time = time / 2; // 소수점은 자동으로 버려짐
            }
            totalTime += time;
        }

        // 주문 정보 업데이트
        this.setOrderTotalPrice(Optional.of(totalPrice));
        this.setEstimatedWaitTime(Optional.of(totalTime));
        this.setStatus("processingCompleted");
    }
}
