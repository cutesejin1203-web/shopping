package com.shopping.comm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders") // order는 DB 예약어라 보통 orders로 씀!
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🚀 포트원과 통신할 고유 주문번호 (예: ORD-171133...)
    @Column(unique = true, nullable = false)
    private String merchantUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int totalAmount; // 찐 결제되어야 할 예상 금액

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 결제 상태

    private String impUid; // 포트원 결제 고유 번호 (사후 검증 후 채워짐)
}

