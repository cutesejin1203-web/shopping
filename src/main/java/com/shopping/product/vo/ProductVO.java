package com.shopping.product.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductVO {
    private Long itemId;
    private String name;
    private int price;
    private int stockQuantity;
    private String description;
    // 🚀 [추가] 카테고리
    private String category;
    
    // 🚀 [추가] 최근 7일 이내 등록 여부 플래그
    private boolean isNew;

    // (이건 리액트에서 받을 때 쓰는 사진 파일들)
    private List<MultipartFile> imageFiles;

    // 리액트로 내려보낼 "대표 사진 1장의 URL"
    private String imgUrl;

    // 프론트에서 전체 사진을 그릴 수 있게 리스트로 담아줄 필드
    private List<ItemImgVO> itemImgList;

    @Data
    public static class ItemImgVO {
        private String imgUrl;
        private String repimgYn;
    }
}
