package com.shopping.order.service;

import com.shopping.order.vo.OrderVO;

import java.util.List;

public interface OrderService {
    // 1. 사전 주문 생성
    OrderVO.ReadyResponse readyOrder(OrderVO.ReadyRequest request, String email);

    // 2. 사후 결제 검증
    boolean verifyPayment(OrderVO.VerifyRequest request);

    // 3. 주문 내역 조회
    List<OrderVO.HistoryResponse> getOrderHistory(String email);
}