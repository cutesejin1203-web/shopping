package com.shopping.comm.entity;

public enum Role {
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "시스템 관리자"),
    SELLER("ROLE_SELLER", "판매 파트너"),   // 나중에 추가될 수 있는 예시
    VIP("ROLE_VIP", "우수 고객");          // 나중에 추가될 수 있는 예시

    private final String key;
    private final String title;

    Role(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public String getKey() { return key; }
}
