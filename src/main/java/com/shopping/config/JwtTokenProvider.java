package com.shopping.config;

import com.shopping.comm.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct; // 만약 javax 로 뜨면 javax.annotation.PostConstruct 로 변경해 줘!
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    // String 대신 안전하게 서명할 수 있는 Key 객체를 사용할 거야
    private Key key;
    private long tokenValidityInMilliseconds = 1000L * 60 * 60; // 1시간 유효

    // [핵심 변경] 스프링이 이 클래스를 생성한 직후에 자동으로 실행되는 메서드!
    // yml에서 가져온 Base64 문자열을 진짜 보안 키(Key 객체)로 변환해 줘.
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public String createToken(String email, Role role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role.name()); // [확장성] 권한 정보를 토큰에 박아버림!

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                // [변경] String이 아니라 우리가 만든 안전한 key 객체를 넣어서 도장을 쾅!
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 사용자 정보(Email) 추출
    public String getEmail(String token) {
        // [변경] 최신 JJWT 라이브러리 방식에 맞게 parserBuilder()를 사용해서 안전하게 해독!
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}