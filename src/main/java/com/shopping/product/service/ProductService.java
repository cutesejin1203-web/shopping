package com.shopping.product.service;

import com.shopping.product.vo.ProductVO;

import java.util.List;

public interface ProductService {
    // 전체 상품 목록 조회
    List<ProductVO> findAllItems();

    // 상품 상세 조회
    ProductVO findOne(Long itemId);
}
