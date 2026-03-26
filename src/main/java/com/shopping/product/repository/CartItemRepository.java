package com.shopping.product.repository;

import com.shopping.comm.entity.CartItem;
import com.shopping.product.vo.CartItemVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItem_ItemId(Long cartId, Long itemId);

        @Query("SELECT new com.shopping.product.vo.CartItemVO(" +
                   "ci.id, " +           // Long cartItemId
                   "i.itemId, " +       // Long itemId
                   "i.name, " +         // String name
                   "i.price, " +        // Integer price
                   "ci.count, " +       // int/Integer quantity (VO와 타입 맞춰야 함)
                   "im.imgUrl) " +      // String imgUrl
                   "FROM CartItem ci " +
                   "JOIN ci.item i " +
                   "LEFT JOIN ItemImg im ON im.item.itemId = i.itemId AND im.repimgYn = 'Y' " +
                   "WHERE ci.cart.id = :cartId")
            List<CartItemVO> findByCartIdFetchItem(@Param("cartId") Long cartId);
}