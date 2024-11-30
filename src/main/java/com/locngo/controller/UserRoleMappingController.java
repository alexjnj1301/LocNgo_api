package com.locngo.controller;

import com.locngo.dto.UserRoleMappingDto;
import com.locngo.entity.UserRoleMapping;
import com.locngo.services.UserRoleMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/role")
public class UserRoleMappingController {
    @Autowired
    private UserRoleMappingService userRoleMappingService;

    @PostMapping
    @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_ADMIN)")
    public UserRoleMapping createMapping(@RequestBody UserRoleMappingDto userRoleMappingDto) {
        return userRoleMappingService.createMapping(userRoleMappingDto);
    }
}
