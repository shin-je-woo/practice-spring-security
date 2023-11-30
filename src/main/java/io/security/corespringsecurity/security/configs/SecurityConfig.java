package io.security.corespringsecurity.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.security.common.AjaxLoginAuthenticationEntryPoint;
import io.security.corespringsecurity.security.common.FormAuthenticationDetailsSource;
import io.security.corespringsecurity.security.filter.AjaxLoginAuthenticationFilter;
import io.security.corespringsecurity.security.handler.*;
import io.security.corespringsecurity.security.manager.CustomAuthorizationManager;
import io.security.corespringsecurity.security.provider.AjaxAuthenticationProvider;
import io.security.corespringsecurity.security.provider.FormAuthenticationProvider;
import io.security.corespringsecurity.security.service.CustomUserDetailsService;
import io.security.corespringsecurity.service.SecurityResourceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authBuilder;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final SecurityResourceService securityResourceService;

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
    @Order(0)
    protected SecurityFilterChain ajaxSeucurityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable);

        http.securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/loginForm").permitAll()
                        .requestMatchers("/api/messages").hasRole("MANAGER")
                        .anyRequest().authenticated());

        http.addFilterBefore(ajaxLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(handler -> handler
                .authenticationEntryPoint(ajaxAuthenticationEntryPoint())
                .accessDeniedHandler(ajaxAccessDeniedHandler()));

        return http.getOrBuild();
    }

    @Bean
    @Order(1)
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(form -> form
                .loginPage("/loginForm")
                .loginProcessingUrl("/login").permitAll()
                .authenticationDetailsSource(authenticationDetailsSource())
                .successHandler(formAuthenticationSuccessHandler())
                .failureHandler(formAuthenticationFailureHandler()));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/loginForm**", "/signup**", "/users").permitAll()
                .requestMatchers("/**").access(new CustomAuthorizationManager(securityResourceService)));


        http.exceptionHandling(handler -> handler
                .accessDeniedHandler(formAccessDeniedHandler()));

        return http.getOrBuild();
    }

    @Bean
    protected UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    protected AuthenticationProvider formAuthenticationProvider() {
        return new FormAuthenticationProvider(userDetailsService(), passwordEncoder());
    }

    @Bean
    protected AuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider(userDetailsService(), passwordEncoder());
    }

    @Bean
    protected AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return new FormAuthenticationDetailsSource();
    }

    @Bean
    protected AuthenticationSuccessHandler formAuthenticationSuccessHandler() {
        return new FormAuthenticationSuccessHandler();
    }

    @Bean
    protected AuthenticationFailureHandler formAuthenticationFailureHandler() {
        return new FormAuthenticationFailureHandler();
    }

    @Bean
    protected AuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
        return new AjaxAuthenticationSuccessHandler(objectMapper);
    }

    @Bean
    protected AuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
        return new AjaxAuthenticationFailureHandler(objectMapper);
    }

    @Bean
    protected AccessDeniedHandler formAccessDeniedHandler() {
        return new FormAccessDeniedHandler("/denied");
    }

    @Bean
    protected AccessDeniedHandler ajaxAccessDeniedHandler() {
        return new AjaxAccessDeniedHandler();
    }

    @Bean
    protected AuthenticationEntryPoint ajaxAuthenticationEntryPoint() {
        return new AjaxLoginAuthenticationEntryPoint();
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
        authBuilder.authenticationProvider(formAuthenticationProvider());
        authBuilder.authenticationProvider(ajaxAuthenticationProvider());
        AjaxLoginAuthenticationFilter ajaxLoginAuthenticationFilter = new AjaxLoginAuthenticationFilter(objectMapper);
        ajaxLoginAuthenticationFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        ajaxLoginAuthenticationFilter.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler());
        ajaxLoginAuthenticationFilter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler());
        ajaxLoginAuthenticationFilter.setSecurityContextRepository(securityContextRepository());
        return ajaxLoginAuthenticationFilter;
    }

    /**
     * 인증필터인 AjaxLoginAuthenticationFilter의 부모 AbstractAuthenticationProcessingFilter의 기본 SecurityContextRepository가 RequestAttributeSecurityContextRepository이기 때문에 SecurityContext가 세션에 저장되지 않는다.
     * 참고로, UsernamePassowordAuthenticationFilter의 부모 AbstractAuthenticationProcessingFilter의 SecurityContextRepository는 DelegatingSecurityContextRepository(HttpSessionSecurityContextRepository(), RequestAttributeSecurityContextRepository())이다.
     * 따라서, 인증필터 AjaxLoginAuthenticationFilter의 부모 AbstractAuthenticationProcessingFilter에 세션을 이용하는 SecurityContextRepository를 따로 등록해줘야 한다.(기본설정이 아니기 때문)
     */
    @Bean
    protected SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new HttpSessionSecurityContextRepository(),
                new RequestAttributeSecurityContextRepository());
    }
}