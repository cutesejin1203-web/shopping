package com.shopping.order.service.impl;

import com.shopping.comm.entity.Member;
import com.shopping.member.repository.MemberRepository;
import com.shopping.comm.entity.Order;
import com.shopping.comm.entity.OrderStatus;
import com.shopping.order.repository.OrderRepository;
import com.shopping.order.service.OrderService;
import com.shopping.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    // 🚀 장바구니 총 금액 계산용 서비스 (CartService에서 만들었다고 가정)
    // private final CartService cartService;

    @Override
    public OrderVO.ReadyResponse readyOrder(OrderVO.ReadyRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        // 1. 🚀 [보안 핵심] DB 기준으로 찐 금액 계산
        // 실무: cartService.calculateTotalAmount(request.getCartId());
        int calculatedAmount = 50000; // (예시) DB 조회해서 계산한 진짜 총액

        if (calculatedAmount != request.getRequestAmount()) {
            throw new IllegalArgumentException("결제 금액이 변조되었습니다!");
        }

        // 2. 고유 주문번호 생성 (실무: 날짜 + 랜덤 문자열)
        String merchantUid = "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);

        // 3. 주문 엔티티 '대기(READY)' 상태로 저장
        Order order = Order.builder()
                .merchantUid(merchantUid)
                .member(member)
                .totalAmount(calculatedAmount)
                .status(OrderStatus.READY)
                .build();

        orderRepository.save(order);

        // 4. 프론트엔드로 응답
        return OrderVO.ReadyResponse.builder()
                .merchantUid(merchantUid)
                .amount(calculatedAmount)
                .buyerEmail(member.getEmail())
                .buyerName(member.getName())
                .build();
    }

    @Override
    public boolean verifyPayment(OrderVO.VerifyRequest request) {
        // 1. 우리 DB에 저장된 사전 주문 정보 조회
        Order order = orderRepository.findByMerchantUid(request.getMerchantUid())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 2. 🚀 포트원 서버에 찐 결제 금액 물어보기 (Server to Server 통신)
        // 실제로는 Iamport API (GET /payments/{imp_uid}) 를 호출해서 가져옴!
        int actualPaidAmount = getAmountFromPortOneAPI(request.getImpUid());

        // 3. 금액 비교 검증
        if (order.getTotalAmount() == actualPaidAmount) {
            // 결제 성공! 상태 변경 및 impUid 업데이트
            order.setStatus(OrderStatus.PAID);
            order.setImpUid(request.getImpUid());

            // 💡 [실무 팁] 여기서 장바구니 비우기, 재고 차감 로직 실행!

            return true;
        } else {
            // 결제 금액이 다름! (위변조 의심)
            order.setStatus(OrderStatus.FAILED);
            log.error("결제 금액 위변조 의심: 주문번호 {}", request.getMerchantUid());

            // 💡 [실무 팁] 포트원 API를 호출해서 '결제 강제 취소(환불)' 처리를 해야 함!
            return false;
        }
    }

    // 포트원 API 연동 가짜 메서드 (실무에선 IamportClient 사용)
    private int getAmountFromPortOneAPI(String impUid) {
        // 포트원 토큰 발급 -> 결제 단건 조회 API 호출 -> 응답받은 amount 반환
        return 50000; // 임시 리턴
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderVO.HistoryResponse> getOrderHistory(String email) {
        // 1. 해당 유저의 모든 주문 내역을 최신순으로 조회
        List<Order> orders = orderRepository.findByMemberEmailOrderByIdDesc(email);

        // 2. Entity -> DTO 변환 (Stream 활용)
        return orders.stream().map(order -> {

            // 💡 [실무 팁] 원래는 order.getOrderItems() 루프 돌면서 매핑해야 함!
            // 지금은 OrderItem 엔티티가 없으니 화면 깨지지 않게 임시 상품을 넣어줄게.
            List<OrderVO.OrderItemDto> dummyItems = new java.util.ArrayList<>();
            dummyItems.add(OrderVO.OrderItemDto.builder()
                    .name("주문 상품 (상세 엔티티 연결 필요)")
                    .price(order.getTotalAmount())
                    .quantity(1)
                    .imgUrl("/assets/no-image.png")
                    .build());

            return OrderVO.HistoryResponse.builder()
                    .orderId(order.getId())
                    // 실무에선 BaseTimeEntity의 getRegTime()을 포맷팅해서 써야 해!
                    .orderDate("2026. 03. 26")
                    .merchantUid(order.getMerchantUid())
                    .status(order.getStatus().name())
                    .totalAmount(order.getTotalAmount())
                    .items(dummyItems)
                    .build();

        }).collect(java.util.stream.Collectors.toList());
    }
}