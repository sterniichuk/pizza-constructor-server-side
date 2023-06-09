package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.dto.OrderDetails;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "order_details")
@Table(name = "order_details")
public class OrderDetailsEntity {
    @Id
    @SequenceGenerator(
            name = "order_details_sequence",
            sequenceName = "order_details_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_details_sequence"
    )
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    ToppingEntity size;

    @ManyToOne(fetch = FetchType.LAZY)
    ToppingEntity dough;


    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    List<ToppingCounter> toppings = new ArrayList<>(0);

    public OrderDetails getDTO() {
        return OrderDetails.builder()
                .dough(dough.getName())
                .size(size.getName())
                .toppings(toppings.stream().map(x->x.getTopping().getName() + " x" + x.getAmount()).toList())
                .build();
    }
}
