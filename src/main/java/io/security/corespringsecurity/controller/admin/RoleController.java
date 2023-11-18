package io.security.corespringsecurity.controller.admin;

import io.security.corespringsecurity.domain.dto.RoleDto;
import io.security.corespringsecurity.domain.entity.Role;
import io.security.corespringsecurity.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/admin/roles")
    public String getRoles(Model model) {

        List<Role> roles = roleService.getRoles();
        model.addAttribute("roles", roles);

        return "admin/role/list";
    }

    @GetMapping("/admin/roles/{id}")
    public String getRole(@PathVariable Long id, Model model) {

        RoleDto roleDto = roleService.getRole(id);
        model.addAttribute("role", roleDto);

        return "admin/role/detail";
    }

    @GetMapping(value = "/admin/roles/register")
    public String viewRoles(Model model) {

        RoleDto role = new RoleDto();
        model.addAttribute("role", role);

        return "admin/role/detail";
    }

    @PostMapping(value="/admin/roles")
    public String createRole(@ModelAttribute RoleDto roleDto) {

        roleService.createRole(roleDto);

        return "redirect:/admin/roles";
    }
}
