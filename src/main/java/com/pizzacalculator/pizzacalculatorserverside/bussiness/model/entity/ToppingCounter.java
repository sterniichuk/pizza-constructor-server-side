package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "topping_counter")
@Table(name = "topping_counter")
public class ToppingCounter {
    @Id
    @SequenceGenerator(
            name = "topping_counter_sequence",
            sequenceName = "topping_counter_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "topping_counter_sequence"
    )
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer amount;

    public void increment(){
        amount++;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private ToppingEntity topping;
}
