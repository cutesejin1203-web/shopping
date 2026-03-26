package com.shopping.order.service;

import com.shopping.order.vo.OrderVO;

public interface OrderService {
    // 1. 사전 주문 생성
    OrderVO.ReadyResponse readyOrder(OrderVO.ReadyRequest request, String email);

    // 2. 사후 결제 검증
    boolean verifyPayment(OrderVO.VerifyRequest request);
}