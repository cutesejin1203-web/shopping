package com.shopping.comm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder // 🚀 이거 넣어야 ServiceImpl에서 .builder() 쓸 수 있어!
@NoArgsConstructor // JPA 기본 생성자
@AllArgsConstructor // Builder 사용을 위한 전체 필드 생성자
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 🚀 추가된 필드들
    @Column(length = 10)
    private String zipCode;

    private String address;

    private String detailAddress;

    @Column(length = 20)
    private String phone;

    @Column(length = 10) // YYYY-MM-DD 형식 대비
    private String birthdate;

    @Column(name = "sms_consent") // DB 컬럼명과 매핑 확인
    private boolean smsConsent;   // String -> boolean으로 변경

    @Column(name = "email_consent")
    private boolean emailConsent; // String -> boolean으로 변
}