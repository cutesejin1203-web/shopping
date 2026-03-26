package com.shopping.member.service;

import com.shopping.member.vo.MemberVO;

public interface MemberService {

    Long join(MemberVO vo) throws Exception;
    // 1. 이메일(아이디)로 내 정보 가져오기
    MemberVO getMemberByEmail(String email);

    // 2. 바뀐 정보로 내 정보 덮어쓰기
    void updateMember(String email, MemberVO memberVO);

}
