package com.shopping.member.service.impl;

import com.shopping.comm.entity.Member; // 엔티티 임포트 확인!
import com.shopping.comm.entity.Role;
import com.shopping.member.repository.MemberRepository;
import com.shopping.member.service.MemberService;
import com.shopping.member.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional // 🚀 가입은 쓰기 작업이니까 readOnly=false(기본값)가 적용되어야 해!
    public Long join(MemberVO vo) throws Exception {

        // 1. 중복 가입 체크
        if (memberRepository.existsByEmail(vo.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화 (VO에 다시 세팅하거나 바로 엔티티에 넣기)
        String encodedPassword = passwordEncoder.encode(vo.getPassword());

        // 3. VO -> Entity 변환 (이게 핵심!)
        // Member 엔티티에 @Builder가 있다면 더 깔끔하지만, 일단 일반적인 방식으로 작성할게.
        Member member = new Member();
        member.setName(vo.getName());
        member.setEmail(vo.getEmail());
        member.setPassword(encodedPassword);
        member.setZipCode(vo.getZipCode());
        member.setAddress(vo.getAddress());
        member.setDetailAddress(vo.getDetailAddress());
        member.setPhone(vo.getPhone());
        member.setBirthdate(vo.getBirthdate());
        member.setSmsConsent(vo.isSmsConsent());
        member.setEmailConsent(vo.isEmailConsent());
        member.setRole(Role.USER);

        // 4. 저장 후 ID 반환
        return memberRepository.save(member).getId();
    }

    @Override
    public MemberVO getMemberByEmail(String email) {
        // 1. DB에서 엔티티 꺼내오기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. VO(택배 상자)에 예쁘게 옮겨 담기
        MemberVO vo = new MemberVO();
        vo.setEmail(member.getEmail());
        vo.setName(member.getName());
        vo.setZipCode(member.getZipCode());
        vo.setAddress(member.getAddress());
        vo.setDetailAddress(member.getDetailAddress());
        vo.setPhone(member.getPhone());
        vo.setBirthdate(member.getBirthdate());

        // 💡 만약 Member 엔티티에 동의 필드가 있다면 주석 해제! (이름 맞는지 확인)
        vo.setSmsConsent(member.isSmsConsent());
        vo.setEmailConsent(member.isEmailConsent());

        return vo; // 3. 리액트로 배달!
    }

    @Override
    @Transactional // 💡 이건 DB를 수정해야 하니까 트랜잭션 걸어줌!
    public void updateMember(String email, MemberVO memberVO) {
        // 1. 기존 엔티티 멱살 잡고 끌고 오기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 리액트가 보낸 새 데이터로 덮어쓰기 (JPA가 알아서 UPDATE 쿼리 쏨!)
        member.setName(memberVO.getName());
        member.setZipCode(memberVO.getZipCode());
        member.setAddress(memberVO.getAddress());
        member.setDetailAddress(memberVO.getDetailAddress());
        member.setPhone(memberVO.getPhone());
        member.setBirthdate(memberVO.getBirthdate());

        // 💡 엔티티에 필드 있으면 주석 해제!
        member.setSmsConsent(memberVO.isSmsConsent());
        member.setEmailConsent(memberVO.isEmailConsent());

        // 3. [초중요] 새 비밀번호를 입력했을 때만 암호화해서 변경! (빈칸이면 기존 비번 유지)
        if (memberVO.getPassword() != null && !memberVO.getPassword().trim().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(memberVO.getPassword());
            member.setPassword(encodedPassword);
        }
    }

}