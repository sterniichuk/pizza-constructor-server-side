package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.OrderEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public final class KitchenTask {
    private final long id;

    private final TimeToFinish timeToFinish;

    private OrderState state;

    private volatile long leftTimeToFinish;

    private final boolean delivery;

    private final List<OrderEntity> tasks;

    public record TimeToFinish(long queue, long cooking, long delivering, long waitingForClient) {
        public long sum() {
            return queue + cooking + delivering + waitingForClient;
        }
    }

    public void decreaseTime(long diff) {
        long newTime = leftTimeToFinish - diff;
        leftTimeToFinish = newTime > 0 ? newTime : 0;
    }
}
