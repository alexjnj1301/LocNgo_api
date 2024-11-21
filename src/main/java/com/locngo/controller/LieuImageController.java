package com.locngo.controller;

import com.locngo.dto.CreateLieuImageDto;
import com.locngo.dto.LieuImageDto;
import com.locngo.services.LieuImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/images/lieu")
public class LieuImageController {
    @Autowired
    private LieuImageService lieuImageService;

    @GetMapping("/{lieuId}")
    public List<LieuImageDto> getLieuImageByLieuId(@PathVariable int lieuId) {
        return lieuImageService.findByLieuId(lieuId);
    }

    @PostMapping
    public void addImageToLieu(@RequestBody CreateLieuImageDto createLieuImageDto) {
        lieuImageService.addImageToLieu(createLieuImageDto);
    }
}
