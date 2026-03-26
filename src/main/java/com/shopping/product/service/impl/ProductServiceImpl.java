package com.shopping.product.service.impl;

import com.shopping.comm.entity.Item;
import com.shopping.comm.entity.ItemImg;
import com.shopping.product.repository.ProductRepository;
import com.shopping.product.service.ProductService;
import com.shopping.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // 🚀 날짜 계산용
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductVO> findAllItems() {
        return productRepository.findAll().stream()
                .map(item -> {
                    ProductVO vo = new ProductVO();
                    vo.setItemId(item.getItemId());
                    vo.setName(item.getName());
                    vo.setPrice(item.getPrice());
                    vo.setStockQuantity(item.getStockQuantity());
                    vo.setDescription(item.getDescription());
                    vo.setCategory(item.getCategory());

                    // 🚀 [추가] 7일 이내 등록 상품이면 isNew = true
                    if (item.getRegDate() != null) {
                        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
                        vo.setNew(item.getRegDate().isAfter(sevenDaysAgo));
                    } else {
                        vo.setNew(false); // 혹시 날짜가 널이면 false
                    }

                    for (ItemImg img : item.getItemImgs()) {
                        if ("Y".equals(img.getRepimgYn())) {
                            vo.setImgUrl(img.getImgUrl());
                            break;
                        }
                    }

                    List<ProductVO.ItemImgVO> voList = item.getItemImgs().stream()
                            .map(img -> {
                                ProductVO.ItemImgVO vo1 = new ProductVO.ItemImgVO();
                                vo1.setImgUrl(img.getImgUrl());
                                vo1.setRepimgYn(img.getRepimgYn());
                                return vo1;
                            }).collect(Collectors.toList());
                    vo.setItemImgList(voList);

                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    public ProductVO findOne(Long itemId) {
        Item item = productRepository.findById(itemId).orElseThrow();
        ProductVO vo = new ProductVO();
        vo.setItemId(item.getItemId());
        vo.setName(item.getName());
        vo.setPrice(item.getPrice());
        vo.setStockQuantity(item.getStockQuantity());
        vo.setDescription(item.getDescription());
        vo.setCategory(item.getCategory());

        // 🚀 [추가] 상세 페이지에서도 NEW 뱃지를 보여주고 싶다면 세팅!
        if (item.getRegDate() != null) {
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            vo.setNew(item.getRegDate().isAfter(sevenDaysAgo));
        }

        for (ItemImg img : item.getItemImgs()) {
            if ("Y".equals(img.getRepimgYn())) {
                vo.setImgUrl(img.getImgUrl());
                break;
            }
        }

        List<ProductVO.ItemImgVO> voList = item.getItemImgs().stream()
                .map(img -> {
                    ProductVO.ItemImgVO vo1 = new ProductVO.ItemImgVO();
                    vo1.setImgUrl(img.getImgUrl());
                    vo1.setRepimgYn(img.getRepimgYn());
                    return vo1;
                }).collect(Collectors.toList());
        vo.setItemImgList(voList);

        return vo;
    }
}