package com.locngo.controller;

import com.locngo.dto.AddServiceToLieuDto;
import com.locngo.dto.LieuDto;
import com.locngo.dto.LieuResponseDto;
import com.locngo.services.LieuService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<LieuResponseDto> findAll() {
        return lieuService.findAll();
    }

    @PostMapping
    public LieuDto createLieu(@RequestBody LieuDto lieuDto) {
        return lieuService.createLieu(lieuDto);
    }

    @PostMapping("{id}/addservice")
    public LieuDto addServicesToLieu(@PathVariable int id, @RequestBody AddServiceToLieuDto addServiceToLieuDto) {
        return lieuService.addServicesToLieu(id, addServiceToLieuDto.servicesId());
    }

    @DeleteMapping("/{id}")
    public void deleteLieu(@PathVariable int id) {
        lieuService.deleteById(id);
    }
}