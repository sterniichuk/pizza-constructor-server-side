package com.pizzacalculator.pizzacalculatorserverside.bussiness.service;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.CartResponse;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderState;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.OrderDetailsEntity;
import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.OrderEntity;
import com.pizzacalculator.pizzacalculatorserverside.security.exception.NotFoundException;
import com.pizzacalculator.pizzacalculatorserverside.security.model.User;
import com.pizzacalculator.pizzacalculatorserverside.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {
    @Mock
    private UserRepository userRepository;

    private CartService service;
    private final OrderEntity cookingOrder = OrderEntity.builder()
            .id(1L)
            .data(OrderDetailsEntity.getDefault())
            .state(OrderState.COOKING)
            .price(BigDecimal.valueOf(192291))
            .amount(2)
            .paid(false)
            .time(LocalDateTime.now())
            .build();

    private final OrderEntity finishedOrder = OrderEntity.builder()
            .id(1L)
            .data(OrderDetailsEntity.getDefault())
            .state(OrderState.ORDER_DONE)
            .price(BigDecimal.valueOf(192291))
            .amount(2)
            .paid(true)
            .time(LocalDateTime.now())
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CartService(null, userRepository, null, null);
    }

    @Test
    void getCart() {
        //given
        User user = new User();
        user.setOrders(List.of(cookingOrder));
        doReturn(Optional.of(user)).when(userRepository).findById(1L);
        //when
        CartResponse response = service.getCart(1L);
        //then
        assertEquals(cookingOrder.getPrice().multiply(BigDecimal.valueOf(2)), response.cartSum());
        assertEquals(response.orders(), List.of(cookingOrder.getDTO()));
    }

    @Test
    void getCartNoUser() {
        // given
        doReturn(Optional.empty()).when(userRepository).findById(1L);

        // when and then
        assertThrows(NotFoundException.class, () -> service.getCart(1L));
    }

    @Test
    void getCartNoUnfinishedOrders() {
        //given
        User user = new User();
        user.setOrders(List.of(finishedOrder));
        doReturn(Optional.of(user)).when(userRepository).findById(1L);
        //when
        CartResponse response = service.getCart(1L);
        //then
        assertEquals(BigDecimal.ZERO, response.cartSum());
        assertEquals(List.of(), response.orders());
    }
}