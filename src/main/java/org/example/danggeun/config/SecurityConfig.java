package org.example.danggeun.config;

import lombok.RequiredArgsConstructor;
import org.example.danggeun.security.CustomLoginSuccessHandler;
import org.example.danggeun.security.CustomOAuth2UserService;
import org.example.danggeun.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final AuthenticationFailureHandler customLoginFailureHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        // 정적 리소스
                        "/css/**", "/js/**", "/images/**", "/asset/**", "/favicon.ico", "/img/**",
                        // Chrome DevTools 관련 요청
                        "/.well-known/**",
                        // 개발 도구 관련
                        "/webjars/**",
                        // API 문서 관련
                        "/swagger-ui/**", "/v3/api-docs/**"
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 완전 비활성화
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // 공개 페이지 (로그인 없이 접근 가능)
                        .requestMatchers(
                                "/", "/login", "/register", "/header",
                                "/error", "/error/**",
                                "/write", "/write/**"  // 임시로 write도 공개 (디버깅용)
                        ).permitAll()

                        // 트레이드 관련 - GET은 공개
                        .requestMatchers(HttpMethod.GET, "/trade", "/trade/**").permitAll()

                        // API 엔드포인트는 인증 필요
                        .requestMatchers("/api/**").authenticated()

                        // 채팅 및 WebSocket은 인증 필요
                        .requestMatchers(
                                "/chat", "/chat/**",
                                "/ws/**", "/ws-chat/**",
                                "/app/**", "/topic/**"
                        ).authenticated()

                        // 검색 기능 공개
                        .requestMatchers("/search", "/search/**").permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(customLoginSuccessHandler)
                        .failureHandler(customLoginFailureHandler)
                        .permitAll()
                )

                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customLoginSuccessHandler)
                        .defaultSuccessUrl("/", true)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider::authenticate;
    }
}