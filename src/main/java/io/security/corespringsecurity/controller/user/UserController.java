package io.security.corespringsecurity.controller.user;

import io.security.corespringsecurity.domain.dto.AccountDto;
import io.security.corespringsecurity.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/mypage")
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
}
