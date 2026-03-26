package com.shopping.product.repository;

import com.shopping.comm.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // 현재 로그인한 사람의 Member ID로 그 사람의 장바구니를 찾아오는 역할
    Cart findByMemberId(Long memberId);
}
