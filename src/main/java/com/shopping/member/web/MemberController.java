package com.shopping.member.web;

import com.shopping.member.service.MemberService;
import com.shopping.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 🚀 1. 내 정보 불러오기 (리액트가 화면 켤 때 GET 요청)
    @GetMapping("/me")
    public ResponseEntity<MemberVO> getMyProfile(Principal principal) {
        // 💡 JWT 토큰이 없거나 이상하면 401(Unauthorized) 에러로 쫓아냄
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        // 토큰에서 까본 이메일(아이디) 꺼내기
        String email = principal.getName();

        // 서비스 시켜서 데이터 가져오기
        MemberVO memberVO = memberService.getMemberByEmail(email);

        return ResponseEntity.ok(memberVO);
    }

    // 🚀 2. 내 정보 수정하기 (리액트가 저장 버튼 누를 때 PUT 요청)
    @PutMapping("/me")
    // 💡 JSON 데이터를 받을 거니까 @RequestBody 필수!
    public ResponseEntity<String> updateMyProfile(Principal principal, @RequestBody MemberVO memberVO) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String email = principal.getName();

        try {
            // 서비스 시켜서 데이터 덮어쓰기
            memberService.updateMember(email, memberVO);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("fail");
        }
    }
}