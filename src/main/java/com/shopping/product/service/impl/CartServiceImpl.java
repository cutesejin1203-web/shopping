package com.shopping.product.service.impl;

import com.shopping.comm.entity.Cart;
import com.shopping.comm.entity.CartItem;
import com.shopping.comm.entity.Item;
import com.shopping.comm.entity.Member;
import com.shopping.member.repository.MemberRepository;
import com.shopping.product.repository.CartItemRepository;
import com.shopping.product.repository.CartRepository;
import com.shopping.product.repository.ProductRepository;
import com.shopping.product.service.CartService;
import com.shopping.product.vo.CartItemVO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Long addCart(CartItemVO requestVO, String email) {
        
        // 1. 어떤 상품을 담을건지 찾기
        Item item = productRepository.findById(requestVO.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        
        // 2. 로그인한 회원 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        // 3. 이 회원의 장바구니 찾기 (없으면 생성)
        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 4. 상품이 이미 장바구니에 있는지 확인 (Item 엔티티의 PK는 getItemId() 야!)
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItem_ItemId(cart.getId(), item.getItemId());

        if (savedCartItem != null) {
            // 이미 있으면 갯수만 올려주기
            savedCartItem.addCount(requestVO.getQuantity());
            return savedCartItem.getId();
        } else {
            // 처음 담는 거라면 새로 만들어서 넣기
            CartItem cartItem = CartItem.createCartItem(cart, item, requestVO.getQuantity());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemVO> getCartList(String email) {
        
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        Cart cart = cartRepository.findByMemberId(member.getId());

        if (cart == null) {
            return new ArrayList<>();
        }

        // 🚀 리포지토리에서 바로 VO 리스트를 가져오기 때문에 루프 돌릴 필요가 없음!
        return cartItemRepository.findByCartIdFetchItem(cart.getId());
    }

    @Override
    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
                
        // 변경 감지(Dirty Checking)로 자동 업데이트 됨
        cartItem.updateCount(count); 
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
                
        cartItemRepository.delete(cartItem);
    }
}