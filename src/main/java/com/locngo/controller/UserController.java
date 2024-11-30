package com.locngo.controller;

import com.locngo.entity.User;
import com.locngo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_ADMIN)")
    public User getUserById(@PathVariable int id) {
        return userService.findById(id);
    }
}
