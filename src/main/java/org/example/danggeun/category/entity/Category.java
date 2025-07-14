// Category.java
package org.example.danggeun.category.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.danggeun.product.entity.Product;

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
    private Long id; // 카테고리ID

    @Column(name = "Category_Nm", length = 50)
    private String name; // 카테고리명

    // 하나의 카테고리는 여러 상품을 가질 수 있음
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();
}
