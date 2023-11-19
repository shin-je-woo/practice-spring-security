package io.security.corespringsecurity.domain.entity;

import io.security.corespringsecurity.domain.dto.ResourcesDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resources {

    @Id
    @GeneratedValue
    @Column(name = "resource_id")
    private Long id;

    private String resourceName;

    private String httpMethod;

    private int orderNum;

    private String resourceType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_resources",
            joinColumns = {@JoinColumn(name = "resource_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> roleList = new ArrayList<>();

    @Builder
    public Resources(Long id, String resourceName, String httpMethod, int orderNum, String resourceType, List<Role> roleList) {
        this.id = id;
        this.resourceName = resourceName;
        this.httpMethod = httpMethod;
        this.orderNum = orderNum;
        this.resourceType = resourceType;
        this.roleList = roleList != null ? roleList : new ArrayList<>();
    }

    public ResourcesDto toDto() {
        return ResourcesDto.builder()
                .id(id)
                .resourceName(resourceName)
                .httpMethod(httpMethod)
                .orderNum(orderNum)
                .resourceType(resourceType)
                .roleList(roleList)
                .build();
    }
}
