package io.security.corespringsecurity.security.manager;

import io.security.corespringsecurity.service.SecurityResourceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.hierarchicalroles.NullRoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.springframework.security.web.util.matcher.RequestMatcher.MatchResult;

@RequiredArgsConstructor
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private final RoleHierarchy roleHierarchy = new NullRoleHierarchy();
    private final SecurityResourceService securityResourceService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {

        HttpServletRequest request = requestAuthorizationContext.getRequest();
        Map<RequestMatcher, List<String>> resourceMap = securityResourceService.getResourceMap();

        for (RequestMatcher requestMatcher : resourceMap.keySet()) {
            MatchResult matchResult = requestMatcher.matcher(request);
            if (matchResult.isMatch()) {
                List<String> authorities = resourceMap.get(requestMatcher);
                boolean granted = isGranted(authentication.get(), authorities);
                return new AuthorityAuthorizationDecision(granted, AuthorityUtils.createAuthorityList(authorities));
            }
        }

        return isAuthenticated(authentication.get());
    }

    private AuthorizationDecision isAuthenticated(Authentication authentication) {
        boolean granted = authentication != null && !this.trustResolver.isAnonymous(authentication)
                && authentication.isAuthenticated();
        return new AuthorizationDecision(granted);
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
