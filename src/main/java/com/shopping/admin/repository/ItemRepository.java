package com.shopping.admin.repository;

import com.shopping.comm.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    // 기본 CRUD 메서드가 자동으로 생성됨!
}