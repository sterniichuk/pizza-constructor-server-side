package com.pizzacalculator.pizzacalculatorserverside.bussiness.service;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.KitchenTask;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderState;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.OrderEntity;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.repository.OrderEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class KitchenService {
    private final ExecutorService executorService;
    private final Map<Long, List<KitchenTask>> kitchenWork = new ConcurrentHashMap<>();


    public void submitTasks(long clientId, List<OrderEntity> orders, boolean delivery) {
        KitchenTask.TimeToFinish time = calculateTime(orders, delivery);
        var task = new KitchenTask(clientId, time, delivery, orders);
        task.setLeftTimeToFinish(time.sum());
        List<KitchenTask> kitchenTasks = kitchenWork.get(clientId);
        if (kitchenTasks == null) {
            ArrayList<KitchenTask> list = new ArrayList<>();
            list.add(task);
            kitchenWork.put(clientId, list);
        } else {
            kitchenTasks.add(task);
        }
        task.setState(orders.get(0).getState());
        executorService.submit(() -> work(task));
    }

    private final OrderEntityRepository orderEntityRepository;
    Logger logger = Logger.getLogger(this.getClass().toString());

    private void work(KitchenTask task) {
        KitchenTask.TimeToFinish timeToFinish = task.getTimeToFinish();
        if (task.getState() == OrderState.COOKING) {
            doStage(task, timeToFinish.cooking(), 2);
            setStatus(OrderState.COOKING_DONE, task);
        }
        logger.info("Done cooking: " + task.getId());
        if (task.isDelivery() && task.getState() == OrderState.COOKING_DONE) {
            setStatus(OrderState.DELIVERING, task);
            doStage(task, timeToFinish.delivering(), 3);
            setStatus(OrderState.DELIVERED, task);
        }
        if (task.getState().ordinal() < OrderState.WAITING_FOR_CLIENT.ordinal()) {
            setStatus(OrderState.WAITING_FOR_CLIENT, task);
            logger.info("Waiting for client: " + task.getId());
            doStage(task, timeToFinish.waitingForClient(), 1);
        }
        setStatus(OrderState.ORDER_DONE, task);
        logger.info("Done for client: " + task.getId());
    }

    private static void doStage(KitchenTask task, long timeToWork, long step) {
        for (long i = timeToWork; i > 0; i -= step) {
            sleep(step * 1000);
            task.decreaseTime(step);
        }
    }

    public void setStatus(OrderState orderState, KitchenTask task) {
        task.setState(orderState);
        List<OrderEntity> tasks = task.getTasks();
        tasks.forEach(x -> x.setState(orderState));
        orderEntityRepository.saveAll(tasks);
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private KitchenTask.TimeToFinish calculateTime(List<OrderEntity> orders, boolean delivery) {
        Collection<List<KitchenTask>> values = kitchenWork.values();
        long queueTime = 0;
        for (var list : values) {
            for (var task : list) {
                if (task.getState().ordinal() < OrderState.ORDER_DONE.ordinal()) {
                    queueTime += task.getLeftTimeToFinish();
                }
            }
        }
        long cookingTime = 0;
        for (var o : orders) {
            long orderTime = o.getAmount() * 5L + calculateTimeForToppings(o);
            cookingTime += orderTime;
        }
        long waiting = cookingTime * 2;
        long delivering = !delivery ? 0 : orders.size() * 3L;
        return new KitchenTask.TimeToFinish(queueTime, cookingTime, delivering, waiting);
    }

    private long calculateTimeForToppings(OrderEntity o) {
        if (o.getData().getToppings().size() > 4) {
            return (long) (o.getAmount() * 2.5);
        }
        return o.getAmount();
    }

    public long getTimeToWait(Long clientId) {
        List<KitchenTask> kitchenTasks = kitchenWork.get(clientId);
        if (kitchenTasks == null || kitchenTasks.isEmpty()) {
            logger.info("everything is done for: " + clientId);
            return 0;
        }
        long max = 0;
        List<KitchenTask> remove = new ArrayList<>();
        for (var task : kitchenTasks) {
            long time = task.getLeftTimeToFinish();
            if (time <= 0 &&
                    task.getState() == OrderState.ORDER_DONE) {
                remove.add(task);
                continue;
            }
            if (time > max) {
                max = (time + 1);
            }
        }
        if (!remove.isEmpty()) {
            kitchenTasks.removeAll(remove);
        }
        for(var task : kitchenTasks){
            if(task.getState() != OrderState.ORDER_DONE && max == 0){
                max += 1;
            }
        }
        return max;
    }
}
