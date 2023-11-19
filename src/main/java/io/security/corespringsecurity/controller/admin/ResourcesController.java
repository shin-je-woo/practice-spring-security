package io.security.corespringsecurity.controller.admin;

import io.security.corespringsecurity.domain.dto.ResourcesDto;
import io.security.corespringsecurity.domain.entity.Role;
import io.security.corespringsecurity.service.ResourcesService;
import io.security.corespringsecurity.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ResourcesController {

    private final ResourcesService resourcesService;
    private final RoleService roleService;

    @GetMapping("/admin/resources")
    public String getResources(Model model) {

        List<ResourcesDto> resourcesDtoList = resourcesService.getResources();
        model.addAttribute("resources", resourcesDtoList);

        return "admin/resource/list";
    }

    @GetMapping("/admin/resources/{id}")
    public String getResources(@PathVariable Long id, Model model) {

        List<Role> roleList = roleService.getRoles();
        ResourcesDto resourcesDto = resourcesService.getResources(id);
        model.addAttribute("roleList", roleList);
        model.addAttribute("resources", resourcesDto);

        return "admin/resource/detail";
    }

    @GetMapping("/admin/resources/register")
    public String viewRoles(Model model) {

        List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);

        ResourcesDto resourcesDto = new ResourcesDto();
        List<Role> roles = new ArrayList<>();
        resourcesDto.setRoleList(roles);
        model.addAttribute("resources", resourcesDto);

        return "admin/resource/detail";
    }

    @PostMapping("/admin/resources")
    public String createResources(ResourcesDto resourcesDto) {

        resourcesService.createResources(resourcesDto);
        return "redirect:/admin/resources";
    }

    @GetMapping("admin/resources/delete/{id}")
    public String removeResources(@PathVariable Long id) {

        resourcesService.deleteResoureces(id);
        return "redirect:/admin/resources";
    }
}
