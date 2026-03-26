package com.shopping.comm.entity;

// 💡 상태값 Enum (주문 대기, 결제 완료, 취소, 실패)
public enum OrderStatus {
    READY, PAID, CANCELLED, FAILED
}
