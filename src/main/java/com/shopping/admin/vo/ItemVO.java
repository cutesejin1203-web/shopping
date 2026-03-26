package com.shopping.admin.vo;

import com.shopping.product.vo.ProductVO;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ItemVO {
    private Long itemId;
    private String name;
    private int price;
    private int stockQuantity;
    private String description;
    // 🚀 [추가] 카테고리
    private String category;

    // 리액트에서 업로드한 사진 파일들을 받기 위한 변수 추가!
    private List<MultipartFile> imageFiles;
    private List<ProductVO.ItemImgVO> itemImgList;
}