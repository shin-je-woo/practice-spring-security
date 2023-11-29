package io.security.corespringsecurity.security.manager;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.hierarchicalroles.NullRoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static org.springframework.security.web.util.matcher.RequestMatcher.MatchResult;


public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final RoleHierarchy roleHierarchy = new NullRoleHierarchy();
    private static final AuthorizationDecision DENY = new AuthorizationDecision(false);

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        HttpServletRequest request = requestAuthorizationContext.getRequest();
        RequestMatcher matcher = new AntPathRequestMatcher("/mypage");
        MatchResult matchResult = matcher.matcher(request);

        if (matchResult.isMatch()) {
            List<String> authorities = List.of("ROLE_USER");
            boolean granted = isGranted(authentication.get(), authorities);
            return new AuthorityAuthorizationDecision(granted, AuthorityUtils.createAuthorityList(authorities));
        }

        return DENY;
    }

    private boolean isGranted(Authentication authentication, Collection<String> authorities) {
        return authentication != null && isAuthorized(authentication, authorities);
    }

    private boolean isAuthorized(Authentication authentication, Collection<String> authorities) {
        for (GrantedAuthority grantedAuthority : getGrantedAuthorities(authentication)) {
            if (authorities.contains(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(Authentication authentication) {
        return this.roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities());
    }
}
