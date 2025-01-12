package com.locngo.controller;

import com.locngo.dto.AddServiceToLieuDto;
import com.locngo.dto.AllLieuResponseDto;
import com.locngo.dto.LieuDto;
import com.locngo.dto.LieuResponseDto;
import com.locngo.services.LieuService;
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
@RequestMapping("/api/lieu")
public class LieuController {
    @Autowired
    private LieuService lieuService;

    @GetMapping("/{id}")
    public LieuResponseDto findById(@PathVariable int id) {
        return lieuService.findById(id);
    }

    @GetMapping
    public List<AllLieuResponseDto> findAll() {
        return lieuService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_PROPRIETOR)")
    public LieuDto createLieu(@RequestBody LieuDto lieuDto) {
        return lieuService.createLieu(lieuDto);
    }

    @PostMapping("{id}/addservice")
    @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_PROPRIETOR)")
    public LieuDto addServicesToLieu(@PathVariable int id, @RequestBody AddServiceToLieuDto addServiceToLieuDto) {
        return lieuService.addServicesToLieu(id, addServiceToLieuDto.servicesId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_PROPRIETOR)")
    public void deleteLieu(@PathVariable int id) {
        lieuService.deleteById(id);
    }
}
