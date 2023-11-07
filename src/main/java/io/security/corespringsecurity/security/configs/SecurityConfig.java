package io.security.corespringsecurity.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.security.common.FormAuthenticationDetailsSource;
import io.security.corespringsecurity.security.filter.AjaxLoginAuthenticationFilter;
import io.security.corespringsecurity.security.handler.CustomAccessDeniedHandler;
import io.security.corespringsecurity.security.handler.CustomAuthenticationFailureHandler;
import io.security.corespringsecurity.security.handler.CustomAuthenticationSuccessHandler;
import io.security.corespringsecurity.security.provider.FormAuthenticationProvider;
import io.security.corespringsecurity.security.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 일반적인 정적자원들의 보안설정 해제
    @Bean
    protected WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/signup", "/logout", "/loginForm*").permitAll()
                .requestMatchers(HttpMethod.POST, "/users", "/api/login").permitAll()
                .requestMatchers("/mypage").hasRole("USER")
                .requestMatchers("/messages").hasRole("MANAGER")
                .requestMatchers("/config").hasRole("ADMIN")
                .anyRequest().authenticated());
        http.formLogin(form -> form
                .loginPage("/loginForm")
                .loginProcessingUrl("/login").permitAll()
                .authenticationDetailsSource(authenticationDetailsSource())
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler()));
        http.exceptionHandling(handler -> handler
                        .accessDeniedHandler(accessDeniedHandler()));
        http.addFilterBefore(ajaxLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.getOrBuild();
    }

    @Bean
    protected UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    protected AuthenticationProvider authenticationProvider(UserRepository userRepository) {
        return new FormAuthenticationProvider(userDetailsService(userRepository), passwordEncoder());
    }

    @Bean
    protected AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return new FormAuthenticationDetailsSource();
    }

    @Bean
    protected AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    protected AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    protected AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler("/denied");
    }

    /**
     * AbstractAuthenticationProcessingFilter는 객체 생성시 afterPropertiesSet() 메서드에 의해 AuthenticationManager가 필수로 지정되어야 한다.(아래 참고)
     * Assert.notNull(this.authenticationManager, "authenticationManager must be specified");
     * AuthenticationManager 는 스프링 시큐리티가 초기화 되면서 생성하고 있는데, AuthenticationManager 를 바로 참조할 수 있는 API 가 제공되지 않는다.
     * 대신에 초기화 때 AuthenticationManager 를 생성한 설정 클래스(AuthenticationConfiguration)를 참조할 수 있다.
     * 즉, AuthenticationConfiguration를 통해 시큐리티가 생성한 AuthenticationManager를 간접적으로 얻을 수 있다.
     */
    @Bean
    protected AjaxLoginAuthenticationFilter ajaxLoginAuthenticationFilter() throws Exception {
        AjaxLoginAuthenticationFilter ajaxLoginAuthenticationFilter = new AjaxLoginAuthenticationFilter(objectMapper);
        ajaxLoginAuthenticationFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        return ajaxLoginAuthenticationFilter;
    }
}
