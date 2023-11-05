package io.security.corespringsecurity.controller.user;

import io.security.corespringsecurity.domain.AccountDto;
import io.security.corespringsecurity.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value="/mypage")
    public String myPage() {
        return "user/mypage";
    }

    @GetMapping("/signup")
    public String signup() {
        return "user/login/signup";
    }

    @PostMapping("/users")
    public String createUser(AccountDto accountDto) {

        userService.createUser(accountDto);
        return "redirect:/";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/login/loginForm";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return "redirect:/login";
    }
}
