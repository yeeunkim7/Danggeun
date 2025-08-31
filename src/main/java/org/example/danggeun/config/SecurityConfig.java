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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 정적리소스/에러/웰노운/스웨거 허용
                        .requestMatchers(
                                "/", "/login", "/register", "/header",
                                "/css/**", "/js/**", "/images/**", "/asset/**",
                                "/favicon.ico", "/error", "/error/**", "/.well-known/**",
                                "/webjars/**", "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll()
                        // 트레이드 상세 페이지는 공개 GET 허용 (목록/상세 미로그인 열람)
                        .requestMatchers(HttpMethod.GET, "/trade/**").permitAll()
                        // STOMP/WebSocket 엔드포인트 등은 인증
                        .requestMatchers("/chat", "/ws-chat/**", "/app/**", "/topic/**", "/api/categories")
                        .authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession() // 세션 고정 공격 방지
                        .invalidSessionUrl("/login") // 무효한 세션 시 로그인 페이지로 이동
                        .maximumSessions(1) // 동시 세션 1개로 제한
                        .maxSessionsPreventsLogin(false) // 새 로그인 시 기존 세션 만료
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email") // 이메일을 username으로 사용
                        .passwordParameter("password")
                        .successHandler(customLoginSuccessHandler)
                        .failureHandler(customLoginFailureHandler)
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customLoginSuccessHandler) // OAuth2도 같은 success handler 사용
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