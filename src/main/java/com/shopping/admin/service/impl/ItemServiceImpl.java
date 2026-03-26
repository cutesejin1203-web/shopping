package com.shopping.admin.service.impl;

import com.shopping.comm.entity.Item;
import com.shopping.comm.entity.ItemImg; // [추가] 사진 엔티티
import com.shopping.admin.repository.ItemRepository;
import com.shopping.admin.service.ItemService;
import com.shopping.product.repository.ProductRepository;
import com.shopping.admin.vo.ItemVO;
import com.shopping.comm.service.GcsService;
import com.shopping.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // [추가]

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final GcsService gcsService;

    @Override
    @Transactional
    // [추가] 구글 업로드 중 에러가 날 수 있으니 throws Exception 추가!
    // (인터페이스 ItemService.java 에도 똑같이 추가해 줘야 해!)
    public void saveItem(ItemVO itemVO) throws Exception {

        Item item;
        boolean isNew = (itemVO.getItemId() == null);

        // 1. [등록 vs 수정 분기] 10년차의 꼼꼼함: 수정 시 예외를 적극 활용 (Fail-Fast)
        if (isNew) {
            item = new Item(); // 신규 등록
        } else {
            // 수정: 영속성 컨텍스트에서 기존 엔티티 조회 (없으면 즉시 에러 발생!)
            item = itemRepository.findById(itemVO.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. ID: " + itemVO.getItemId()));
        }

        // 2. 데이터 바인딩 
        // (수정일 경우, 트랜잭션 종료 시 JPA의 변경 감지(Dirty Checking)가 동작하여 자동 UPDATE 쿼리가 발생함)
        item.setName(itemVO.getName());
        item.setPrice(itemVO.getPrice());
        item.setStockQuantity(itemVO.getStockQuantity());
        item.setDescription(itemVO.getDescription());
        // 🚀 [추가] 카테고리 바인딩!
        item.setCategory(itemVO.getCategory());

        // ==========================================
        // 🚀 2. [신규 로직] 구글 클라우드 사진 업로드 & DB 저장
        // ==========================================
        List<MultipartFile> imageFiles = itemVO.getImageFiles(); // 리액트가 보낸 사진들 꺼내기

        // 사진 파일이 하나라도 들어왔다면?
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);

                if (!file.isEmpty()) {
                    // 구글 클라우드에 사진을 쏘고, 공용 URL 받아오기!
                    String imgUrl = gcsService.uploadImage(file);

                    // 받아온 구글 URL을 DB(ItemImg 테이블)에 저장 준비
                    ItemImg itemImg = new ItemImg();
                    itemImg.setImgUrl(imgUrl); // 구글 주소 세팅

                    // 첫 번째 올린 사진(i == 0)만 메인 화면용 썸네일(Y)로 지정!
                    itemImg.setRepimgYn(i == 0 ? "Y" : "N");

                        // 3. [10년차 JPA 꿀팁] 영속성 전이(Cascade) 활용
                        // 타 레포지토리(productRepository)를 호출할 필요 없이,
                        // 부모(Item)의 리스트에 추가만 하면 Item이 저장될 때 연관된 사진도 같이 INSERT 됨!
                        itemImg.setItem(item);
                        item.getItemImgs().add(itemImg);
                }
            }
        }
            
        // 4. 저장 (신규 등록 시에만 명시적 호출, 수정 시에는 생략해도 트랜잭션 종료 시 반영됨)
        if (isNew) {
            itemRepository.save(item);
        }
    }

    @Override
    public ItemVO getItem(Long itemId) {
        // 1. DB에서 상품 엔티티 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 없습니다. ID: " + itemId));

        // 2. 리액트가 먹기 좋게 엔티티(Item)를 그릇(ItemVO)에 옮겨 담기
        ItemVO itemVO = new ItemVO();
        itemVO.setItemId(item.getItemId()); // 💡 DB 컬럼명에 따라 getId() 일 수도 있음!
        itemVO.setName(item.getName());
        itemVO.setPrice(item.getPrice());
        itemVO.setStockQuantity(item.getStockQuantity());
        itemVO.setDescription(item.getDescription());
        itemVO.setCategory(item.getCategory());

        // 3. 사진 정보도 리액트에 띄워줘야 하니까 같이 담아주기!
        if (item.getItemImgs() != null) {
            List<ProductVO.ItemImgVO> imgVOList = item.getItemImgs().stream()
                .map(img -> {
                    // 💡 파라미터 3개짜리 생성자 대신, 기본 생성자로 만들고 Setter로 쏙쏙!
                    ProductVO.ItemImgVO imgVO = new ProductVO.ItemImgVO();
                    imgVO.setImgUrl(img.getImgUrl());
                    imgVO.setRepimgYn(img.getRepimgYn());
                    return imgVO;
                })
                .collect(Collectors.toList());

            itemVO.setItemImgList(imgVOList);
        }


        return itemVO;
    }

    @Override
    @Transactional
    public void updateItem(ItemVO itemVO) throws Exception {

        // 1. [검증] 영속성 컨텍스트에서 기존 엔티티 멱살 잡고 끌고 오기
        Item item = itemRepository.findById(itemVO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. ID: " + itemVO.getItemId()));

        // 2. [텍스트 수정] 더티 체킹(Dirty Checking) 활용!
        // 여기서 값을 바꿔주기만 하면, 트랜잭션 끝날 때 JPA가 알아서 UPDATE 쿼리를 날려줘.
        item.setName(itemVO.getName());
        item.setPrice(itemVO.getPrice());
        item.setStockQuantity(itemVO.getStockQuantity());
        item.setDescription(itemVO.getDescription());
        item.setCategory(itemVO.getCategory());

        // ==========================================
        // 🚀 3. [사진 수정 로직] 새 사진이 들어왔을 때만 동작!
        // ==========================================
        List<MultipartFile> imageFiles = itemVO.getImageFiles();

        // 리액트에서 폼을 보낼 때, 빈 파일이 넘어올 수도 있으니 확실하게 필터링!
        boolean hasNewImages = imageFiles != null && !imageFiles.isEmpty()
                                && imageFiles.stream().anyMatch(f -> !f.isEmpty());

        if (hasNewImages) {
            // [A] 기존 사진 흔적 지우기 (GCS 클라우드 + DB)
            if (item.getItemImgs() != null) {
                // 1) 구글 클라우드에서 실제 사진 파일들 펑펑 날리기
                for (ItemImg itemImg : item.getItemImgs()) {
                    if (itemImg.getImgUrl() != null) {
                        try {
                            gcsService.deleteImage(itemImg.getImgUrl());
                        } catch (Exception e) {
                            System.err.println("GCS 기존 이미지 삭제 실패 (수동 확인 필요): " + itemImg.getImgUrl());
                        }
                    }
                }
                // 2) DB에서 기존 이미지 데이터 날리기
                // 💡 Item 엔티티의 itemImgs 필드에 orphanRemoval = true 가 걸려있다면,
                // 이렇게 리스트를 비워주기만 해도 DB에서 자동으로 DELETE 쿼리가 나감!
                item.getItemImgs().clear();
            }

            // [B] 새 사진 올리기 (saveItem 로직 재활용)
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);

                if (!file.isEmpty()) {
                    // 구글 클라우드에 새 사진 쏘고 주소 받아오기
                    String imgUrl = gcsService.uploadImage(file);

                    ItemImg itemImg = new ItemImg();
                    itemImg.setImgUrl(imgUrl);
                    itemImg.setRepimgYn(i == 0 ? "Y" : "N"); // 첫 번째 사진 썸네일 지정
                    itemImg.setItem(item);

                    // 부모 객체에 새 자식(사진) 쏙 넣어주기
                    item.getItemImgs().add(itemImg);
                }
            }
        }
        // 💡 else { 사진을 안 바꿨다면? 기존 사진 리스트(item.getItemImgs())가 그대로 유지됨! }
    }
    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        // 5. [안전한 삭제] 존재하는지 먼저 검증 후 엔티티 단위로 지우기
        // (부모 Item을 삭제하면 연관된 ItemImg 데이터들도 DB에서 자동으로 싹 다 지워짐!)
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 상품을 찾을 수 없습니다. ID: " + itemId));
        
        // ==========================================
        // 🚀 [추가] DB 지우기 전에 GCS 클라우드의 진짜 사진 파일부터 싹 날려주기!
        // ==========================================
        if (item.getItemImgs() != null) {
            for (ItemImg itemImg : item.getItemImgs()) {
                if (itemImg.getImgUrl() != null) {
                    try {
                        gcsService.deleteImage(itemImg.getImgUrl()); // 👈 GcsService에 삭제 메서드 호출!
                    } catch (Exception e) {
                        // 💡 10년차 꿀팁: 사진 삭제 실패했다고 DB 삭제까지 막히면 안 되니까 로그만 남기고 패스!
                        System.err.println("GCS 이미지 삭제 실패 (수동 확인 필요): " + itemImg.getImgUrl());
                    }
                }
            }
        }

        itemRepository.delete(item);
    }
}