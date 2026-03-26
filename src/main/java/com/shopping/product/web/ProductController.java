package com.shopping.product.web;

import com.shopping.product.service.ProductService;
import com.shopping.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/items")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 목록 조회
    @GetMapping
    public List<ProductVO> list() {
        return productService.findAllItems();
    }

}