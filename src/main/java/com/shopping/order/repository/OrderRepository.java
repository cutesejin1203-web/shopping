package com.shopping.order.repository;

import com.shopping.comm.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMerchantUid(String merchantUid);

    // 🚀 [추가] 로그인한 회원의 주문 내역 최신순으로 가져오기
    List<Order> findByMemberEmailOrderByIdDesc(String email);
}