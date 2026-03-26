package com.shopping.order.vo;

import lombok.*;

public class OrderVO {

    // 1. 프론트 -> 백엔드: 사전 주문 생성 요청
    @Getter @Setter
    public static class ReadyRequest {
        private Long cartId;       // 장바구니 통째로 결제할 경우
        private int requestAmount; // 클라이언트가 화면에서 본 금액 (검증용)
    }

    // 2. 백엔드 -> 프론트: 사전 주문 생성 응답 (이걸로 결제창 띄움)
    @Getter @Builder
    public static class ReadyResponse {
        private String merchantUid; // 생성된 주문번호
        private int amount;         // 검증된 진짜 금액
        private String buyerEmail;
        private String buyerName;
    }

    // 3. 프론트 -> 백엔드: 결제 완료 후 사후 검증 요청
    @Getter @Setter
    public static class VerifyRequest {
        private String impUid;      // 포트원 결제 고유번호
        private String merchantUid; // 우리 서버 주문번호
    }
}