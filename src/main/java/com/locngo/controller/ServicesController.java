package com.locngo.controller;

import com.locngo.dto.CreateServicesDto;
import com.locngo.dto.ServicesDto;
import com.locngo.services.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_PROPRIETOR)")
public class ServicesController {
    @Autowired
    private ServicesService servicesService;

    @GetMapping("/{id}")
    public ServicesDto getServiceById(@PathVariable int id) {
        return servicesService.findById(id);
    }

    @GetMapping
    public List<ServicesDto> findAll() {
        return servicesService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_ADMIN)")
    public ServicesDto createService(@RequestBody CreateServicesDto servicesDto) {
        return servicesService.createService(servicesDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_ADMIN)")
    public void deleteService(@PathVariable int id) {
        servicesService.deleteById(id);
    }
}
