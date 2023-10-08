package io.security.basicsecurity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        .anyRequest() // 모든 요청은
                        .authenticated() // 인증된 사용자만 접근 가능
                )
                .formLogin(formConfig -> formConfig
//                        .loginPage("/loginPage") // 로그인 페이지 URL 설정 가능
                                .defaultSuccessUrl("/") // 로그인 성공시 이동할 경로
                                .failureUrl("/login") // 로그인 실패시 이동할 경로
                                .usernameParameter("userId")
                                .passwordParameter("passwd")
                                .loginProcessingUrl("/login_proc") // 로그인을 처리할 URL
                                .successHandler(new AuthenticationSuccessHandler() {
                                    @Override
                                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                        // 로그인 성공하면 실행
                                        log.info("authentication {}", authentication.getName());
                                        response.sendRedirect("/");
                                    }
                                })
                                .failureHandler(new AuthenticationFailureHandler() {
                                    @Override
                                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                                        // 로그인 실패하면 실행
                                        log.info("exception {}", exception.getMessage());
                                        response.sendRedirect("login");
                                    }
                                })
                                .permitAll() // 이 경로는 인증 없이 접근 가능
                )
                .logout(logoutConfig -> logoutConfig
                        .logoutUrl("/logout") // 로그아웃을 처리할 URL (POST메서드로 동작)
                        .logoutSuccessUrl("/login") // 로그아웃 성공시 이동할 경로
                        .addLogoutHandler(new LogoutHandler() {
                            @Override
                            public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                                // LogoutHandler는 여러개 있는데, 커스텀도 가능
                                HttpSession session = request.getSession();
                                session.invalidate();
                            }
                        })
                        .logoutSuccessHandler(new LogoutSuccessHandler() {
                            @Override
                            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                // 로그아웃 성공시 실행
                                response.sendRedirect("/login");
                            }
                        })
                        .deleteCookies("remember-me") // 해당 쿠키를 지운다.
                )
                .rememberMe(rememberConfig -> rememberConfig
                        .rememberMeParameter("remember")
                        .tokenValiditySeconds(3600) // 토큰의 유효 시간(단위:초)
                        .userDetailsService(userDetailsService)
                )
                .sessionManagement(sessionConfig -> sessionConfig
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .sessionManagement(sessionConfig -> sessionConfig
                        .sessionFixation()
                        .changeSessionId()
                )
                .build();
    }

}
