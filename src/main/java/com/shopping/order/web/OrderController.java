package com.shopping.order.web;

import com.shopping.order.service.OrderService;
import com.shopping.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. 결제창 띄우기 전, 사전 주문 생성 (데이터 세팅용)
    @PostMapping("/ready")
    public ResponseEntity<OrderVO.ReadyResponse> readyOrder(
            @RequestBody OrderVO.ReadyRequest request,
            Principal principal) {
        // principal.getName()을 통해 현재 로그인한 이메일을 넘김
        OrderVO.ReadyResponse response = orderService.readyOrder(request, principal.getName());
        return ResponseEntity.ok(response);
    }

    // 2. 프론트에서 결제 완료 후, 검증 요청
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody OrderVO.VerifyRequest request) {
        boolean isVerified = orderService.verifyPayment(request);

        if (isVerified) {
            return ResponseEntity.ok("결제가 성공적으로 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("결제 검증에 실패했습니다. (위변조 의심)");
        }
    }

    // 🚀 [추가] 3. 내 주문 내역 조회 API
    @GetMapping
    public ResponseEntity<List<OrderVO.HistoryResponse>> getOrderHistory(Principal principal) {
        // principal.getName() 에는 로그인한 유저의 이메일이 들어있어!
        List<OrderVO.HistoryResponse> history = orderService.getOrderHistory(principal.getName());
        return ResponseEntity.ok(history);
    }
}