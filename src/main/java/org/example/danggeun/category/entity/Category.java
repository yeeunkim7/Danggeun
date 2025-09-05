package org.example.danggeun.category.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.trade.entity.Trade;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Category_ID")
    private Long id;

    @Column(name = "Category_Nm", length = 50)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Trade> products = new ArrayList<>();
}
