package com.shopping.comm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "itemimg")
@Getter
@Setter
public class ItemImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="item_img_id")
    private Long id;

    private String imgUrl;
    private String repimgYn;

    // 🚀 [핵심 수정] 그냥 숫자가 아니라 진짜 짝꿍(Item)을 명시해야 양방향이 완성돼!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void updateItemImg(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}