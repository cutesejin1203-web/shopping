package com.shopping.product.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemVO {

    private Long cartItemId; // ci.id (Long)
    private Long itemId;     // i.itemId (Long)
    private String name;     // i.name (String)
    private Integer price;   // i.price (Integer)
    private Integer quantity; // ci.count (Integer로 변경 추천!)
    private String imgUrl;   // im.imgUrl (String)

}