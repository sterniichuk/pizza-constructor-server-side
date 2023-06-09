package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "toppings")
@Table(name = "toppings")
public class ToppingEntity {
    @Id
    @SequenceGenerator(
            name = "topping_sequence",
            sequenceName = "topping_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "topping_sequence"
    )
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "numeric(10,2) default 0.00")
    private BigDecimal price;

    @Column(nullable = false)
    private Integer max;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private ToppingCategoryEntity category;
}


