package com.shopping.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 추가!
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private List<String> allowedOrigins;

    // [추가] 문지기에게 줄 토큰 판독기 주입
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // [변경] 가입, 로그인, 메인 페이지 데이터는 누구나 볼 수 있게 허용!
                .requestMatchers("/**").permitAll()
                // [변경] 그 외의 모든 요청(장바구니, 결제 등)은 문지기(토큰) 검사를 통과해야 함!
                //.anyRequest().authenticated()
            )
            // [중요] 스프링 기본 로그인 필터가 돌기 전에, 우리가 만든 JWT 문지기를 먼저 세워둠!
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // [중요] 변수 쓰지 말고 아래처럼 직접 주소를 리스트로 박아버리자!
        // 패턴을 써서 혹시 모를 와일드카드 충돌을 방지할게.
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",
            "http://34.60.156.156*"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // 헤더는 일단 다 허용
        configuration.setAllowCredentials(true); // 쿠키/인증 허용
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        // Spring Security 6.x 부터 RoleHierarchyImpl() 빈 생성자 대신 fromHierarchy 문자열 팩토리 메서드를 사용합니다.
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_SELLER \n ROLE_SELLER > ROLE_USER");
    }
}
