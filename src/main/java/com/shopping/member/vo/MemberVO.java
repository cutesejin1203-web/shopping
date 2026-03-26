package com.shopping.member.vo;

import lombok.Data;

@Data
public class MemberVO {
    private String email;          // 아이디 (수정 불가지만 읽기용으로 필요)
    private String name;           // 이름
    private String password;       // 새 비밀번호 (변경할 때만 값이 들어옴!)
    private String zipCode;        // 우편번호
    private String address;        // 기본주소
    private String detailAddress;  // 상세주소
    private String phone;          // 전화번호
    private String birthdate;      // 생년월일
    private boolean smsConsent;    // SMS 수신동의
    private boolean emailConsent;  // 이메일 수신동의
}