package com.shopping.admin.web;

import com.shopping.admin.service.ItemService;
import com.shopping.admin.vo.ItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
public class AdminController {

    private final ItemService itemService;

    // 🚀 1. 상품 등록 (리액트에서 POST로 쏠 때)
    @PostMapping
    public String save(@ModelAttribute ItemVO itemVO) {
        try {
            itemService.saveItem(itemVO);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // 🚀 4. 기존 상품 정보 불러오기 (리액트 수정 폼 렌더링용!)
    @GetMapping("/{id}")
    public ItemVO getItem(@PathVariable Long id) {
        return itemService.getItem(id); // 서비스에서 DB 데이터 꺼내오기!
    }

    // 🚀 2. 상품 수정 (리액트에서 PUT으로 쏠 때, 주소에 {id}가 들어옴!)
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ItemVO itemVO) {
        try {
            // 💡 프론트에서 넘어온 파일+데이터에 현재 상품의 고유 ID를 딱 박아줌!
            // (ItemVO 안에 id 필드명이 itemId인지 id인지 맞춰서 적어줘!)
            itemVO.setItemId(id);

            // 💡 서비스에 수정 전용 로직(updateItem)을 새로 만들거나 연결해 줘야 해!
            // (기존 이미지 지우고 새 이미지 넣는 등의 처리)
            itemService.updateItem(itemVO);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // 🚀 3. 특정 상품 삭제
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        itemService.deleteItem(id); 
        return "deleted";
    }
}