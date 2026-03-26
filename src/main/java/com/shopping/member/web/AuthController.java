package com.shopping.member.web;

import com.shopping.comm.entity.Member;
import com.shopping.config.JwtTokenProvider;
import com.shopping.member.repository.MemberRepository;
import com.shopping.member.service.MemberService;
import com.shopping.member.vo.LoginRequestVO;
import com.shopping.member.vo.LoginResponseVO;
import com.shopping.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody MemberVO vo) {
        try {
            Long memberId = memberService.join(vo);
            return ResponseEntity.ok(memberId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestVO vo) {
        Member member = memberRepository.findByEmail(vo.getEmail())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        // [핵심] 암호화된 비번이랑 입력한 비번이랑 비교!
        if (!passwordEncoder.matches(vo.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 시 토큰 발행
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole());

        // 리액트한테 토큰이랑 간단한 유저 정보를 보내줘
        return ResponseEntity.ok(new LoginResponseVO(token, member.getName(), member.getRole()));
    }
}
