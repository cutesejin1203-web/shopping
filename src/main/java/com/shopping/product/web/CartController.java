package com.shopping.product.web;

import com.shopping.product.service.CartService;
import com.shopping.product.vo.CartItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    // 🛒 1. 장바구니에 담기
    @PostMapping("/")
    public ResponseEntity addCart(@RequestBody @Validated CartItemVO cartItemVO,
                                  BindingResult bindingResult, 
                                  Authentication authentication) {

        // 로그인 안 한 유저는? 얄짤없지
        if (authentication == null) {
            return new ResponseEntity("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> 
                sb.append(error.getDefaultMessage()).append("\n")
            );
            return new ResponseEntity(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            // Spring Security의 Name(이메일) 꺼내오기
            String email = authentication.getName(); 
            Long cartItemId = cartService.addCart(cartItemVO, email);
            return new ResponseEntity(cartItemId, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity("장바구니 담기에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 📋 2. 내 장바구니 조회하기
    @GetMapping("/")
    public ResponseEntity getCartList(Authentication authentication) {
        
        if (authentication == null) {
            return new ResponseEntity("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        String email = authentication.getName();
        List<CartItemVO> cartItems = cartService.getCartList(email);
        
        return new ResponseEntity(cartItems, HttpStatus.OK);
    }

    // 🔢 3. 수량 변경하기
    @PutMapping("/{cartItemId}")
    public ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId, 
                                         @RequestBody CartItemVO requestVO,
                                         Authentication authentication) {

        if (authentication == null) {
            return new ResponseEntity("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        if (requestVO.getQuantity() <= 0) {
            return new ResponseEntity("최소 1개 이상 담아주세요.", HttpStatus.BAD_REQUEST);
        }

        try {
            cartService.updateCartItemCount(cartItemId, requestVO.getQuantity());
            return new ResponseEntity(cartItemId, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity("수량 변경에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 🗑️ 4. 장바구니에서 삭제하기
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, 
                                         Authentication authentication) {

        if (authentication == null) {
            return new ResponseEntity("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            cartService.deleteCartItem(cartItemId);
            return new ResponseEntity(cartItemId, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity("삭제에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}