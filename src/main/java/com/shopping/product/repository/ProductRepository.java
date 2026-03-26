package com.shopping.product.repository;

import com.shopping.comm.entity.Item;
// ItemImg는 여기서 이제 안 써도 돼!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// 🚀 [핵심 수정] ItemImg가 아니라 진짜 상품인 Item을 관리하게 변경!
public interface ProductRepository extends JpaRepository<Item, Long> {

}