package com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity;

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
@Entity(name = "topping_category")
@Table(name = "topping_category")
public class ToppingCategoryEntity {
    @Id
    @SequenceGenerator(
            name = "topping_category_sequence",
            sequenceName = "topping_category_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "topping_category_sequence"
    )
    @Column(name = "id")
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    private List<ToppingEntity> toppings = new ArrayList<>(0);


    public ToppingCategoryEntity(String name) {
        this.name = name;
    }

    public void addTopping(ToppingEntity t){
        t.setCategory(this);
        toppings.add(t);
    }
}
