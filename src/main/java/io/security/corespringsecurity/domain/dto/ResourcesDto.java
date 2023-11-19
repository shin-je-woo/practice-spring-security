package io.security.corespringsecurity.domain.dto;

import io.security.corespringsecurity.domain.entity.Resources;
import io.security.corespringsecurity.domain.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResourcesDto {

    private Long id;
    private String resourceName;
    private String httpMethod;
    private int orderNum;
    private String resourceType;
    private String roleName;
    private List<Role> roleList;

    @Builder
    public ResourcesDto(Long id, String resourceName, String httpMethod, int orderNum, String resourceType, String roleName, List<Role> roleList) {
        this.id = id;
        this.resourceName = resourceName;
        this.httpMethod = httpMethod;
        this.orderNum = orderNum;
        this.resourceType = resourceType;
        this.roleName = roleName;
        this.roleList = roleList;
    }

    public Resources toEntity() {
        return Resources.builder()
                .resourceName(resourceName)
                .httpMethod(httpMethod)
                .orderNum(orderNum)
                .resourceType(resourceType)
                .build();
    }
}
