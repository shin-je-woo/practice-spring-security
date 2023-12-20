package io.security.corespringsecurity.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.config.JwtProperties;
import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.security.filter.AjaxLoginAuthenticationFilter;
import io.security.corespringsecurity.security.handler.ajax.AjaxAuthenticationFailureHandler;
import io.security.corespringsecurity.security.handler.form.FormAccessDeniedHandler;
import io.security.corespringsecurity.security.handler.jwt.JwtPublisher;
import io.security.corespringsecurity.security.manager.CustomAuthorizationManager;
import io.security.corespringsecurity.security.provider.AjaxAuthenticationProvider;
import io.security.corespringsecurity.security.service.CustomUserDetailsService;
import io.security.corespringsecurity.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class JwtSecurityConfig {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authBuilder;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final SecurityResourceService securityResourceService;
    private final JwtProperties jwtProperties;

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

        http.csrf(CsrfConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/loginForm**", "/signup**", "/users").permitAll()
                .requestMatchers("/**").access(new CustomAuthorizationManager(securityResourceService, roleHierarchy())));

        http.addFilterBefore(ajaxLoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(handler -> handler
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/loginForm"))
                .accessDeniedHandler(new FormAccessDeniedHandler("/denied")));

        return http.getOrBuild();
    }

    @Bean
    protected RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        Map<String, List<String>> roleHierarchyMap = new HashMap<>();

        roleHierarchyMap.put("ROLE_ADMIN", List.of("ROLE_MANAGER"));
        roleHierarchyMap.put("ROLE_MANAGER", List.of("ROLE_USER"));
        String roleHierarchyFromMap = RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);

        roleHierarchy.setHierarchy(roleHierarchyFromMap);
        return roleHierarchy;
    }

    @Bean
    protected AjaxLoginAuthenticationFilter ajaxLoginAuthenticationFilter() throws Exception {
        authBuilder.authenticationProvider(ajaxAuthenticationProvider());
        AjaxLoginAuthenticationFilter ajaxLoginAuthenticationFilter = new AjaxLoginAuthenticationFilter(objectMapper);
        ajaxLoginAuthenticationFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        ajaxLoginAuthenticationFilter.setAuthenticationSuccessHandler(new JwtPublisher(objectMapper, jwtProperties));
        ajaxLoginAuthenticationFilter.setAuthenticationFailureHandler(new AjaxAuthenticationFailureHandler(objectMapper));
        ajaxLoginAuthenticationFilter.setSecurityContextRepository(new RequestAttributeSecurityContextRepository());
        return ajaxLoginAuthenticationFilter;
    }

    @Bean
    protected AuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider(userDetailsService(), passwordEncoder());
    }

    @Bean
    protected UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }
}
