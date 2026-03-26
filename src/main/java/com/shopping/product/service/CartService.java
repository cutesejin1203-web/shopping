package com.shopping.product.service;

import com.shopping.product.vo.CartItemVO;
import java.util.List;

public interface CartService {

    // 1️⃣ 장바구니에 담기 🛒
    Long addCart(CartItemVO requestVO, String email);

    // 2️⃣ 내 장바구니 리스트 보기 📋
    List<CartItemVO> getCartList(String email);

    // 3️⃣ 장바구니 수량 변경 🔢
    void updateCartItemCount(Long cartItemId, int count);

    // 4️⃣ 장바구니에서 빼버리기 🗑️
    void deleteCartItem(Long cartItemId);
}