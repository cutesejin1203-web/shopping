package com.shopping.admin.service;

import com.shopping.admin.vo.ItemVO;

import java.util.List;

public interface ItemService {
    // 상품 등록
    void saveItem(ItemVO itemVO) throws Exception;

    //상품 수정
    void updateItem(ItemVO itemVO) throws Exception;

    // 특정 상품 하나만 조회해서 가져오기 (수정 폼 데이터 뿌리기용)
    ItemVO getItem(Long itemId);

    // 상품 삭제
    void deleteItem(Long itemId);
}
