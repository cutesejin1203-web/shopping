package com.shopping.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// 요청이 올 때마다 한 번씩 실행되는 문지기!
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 헤더에서 토큰(출입증) 꺼내기
        String token = resolveToken(request);

        // 2. 토큰이 있고, 유효하다면?
        if (token != null) {
            try {
                // 토큰에서 이메일 추출
                String email = jwtTokenProvider.getEmail(token);

                // 스프링 시큐리티에게 "이 사람 인증된 회원이야!" 라고 알려줌
                UserDetails userDetails = User.withUsername(email).password("").authorities("ROLE_USER").build();
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                logger.error("토큰 검증 실패: " + e.getMessage());
            }
        }

        // 3. 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 "Bearer " 글자를 떼고 순수 토큰만 가져오는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}