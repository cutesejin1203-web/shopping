package com.shopping.payment.web;

import com.shopping.comm.entity.Orders;
import com.shopping.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final OrderService orderService;
    private final PortOneService portOneService;

    public PaymentController(OrderService orderService, PortOneService portOneService) {
        this.orderService = orderService;
        this.portOneService = portOneService;
    }

    @PostMapping("/verify/{imp_uid}")
    public ResponseEntity<?> verifyPayment(
            @PathVariable("imp_uid") String impUid,
            @RequestBody Map<String, String> request) {

        String merchantUid = request.get("merchant_uid");

        try {
            // 1. 포트원 인증 토큰 발급
            String token = portOneService.getToken();

            // 2. 포트원 결제내역 단건조회 API 호출
            Map<String, Object> paymentData = portOneService.getPaymentData(impUid, token);

            // 3. 결제 금액 검증
            BigDecimal paidAmount = new BigDecimal(paymentData.get("amount").toString());
            Orders order = orderService.getOrder(merchantUid);
            BigDecimal orderAmount = order.getTotalAmount();

            if (paidAmount.compareTo(orderAmount) == 0) {
                // 결제 금액 일치: 결제 성공 처리
                orderService.updateOrderPaymentSuccess(merchantUid, impUid);
                return ResponseEntity.ok(Map.of("status", "success", "message", "Payment verified and order completed."));
            } else {
                // 결제 금액 불일치: 위변조 의심 (실무에서는 결제 취소 API 호출 필요)
                orderService.updateOrderPaymentFailed(merchantUid);
                return ResponseEntity.badRequest().body(Map.of("status", "forgery", "message", "Payment amount mismatch."));
            }

        } catch (Exception e) {
            orderService.updateOrderPaymentFailed(merchantUid);
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
