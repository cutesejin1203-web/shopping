package com.shopping.comm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    // 어떤 장바구니에 담긴건지 (Cart랑 N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // 어떤 상품을 담은건지 (Item이랑 N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 담은 갯수
    private int count;

    // 장바구니에 새 상품 담을 때 편하게 쓰는 세팅 메서드
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    // 이미 담겨있던 거면 갯수만 늘리는 녀석
    public void addCount(int count) {
        this.count += count;
    }

    // 장바구니 화면에서 수량 바꿀 때
    public void updateCount(int count) {
        this.count = count;
    }
}
