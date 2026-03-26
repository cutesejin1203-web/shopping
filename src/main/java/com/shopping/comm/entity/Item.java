package com.shopping.comm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item")
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 🚀 [추가] 카테고리
    @Column(length = 50)
    private String category;

    @CreationTimestamp
    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    // 양방향 관계 설정: Item에서도 ItemImg 목록을 참조할 수 있게 함
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImg> itemImgs = new ArrayList<>();
}
