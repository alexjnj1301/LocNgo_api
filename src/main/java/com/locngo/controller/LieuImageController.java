package com.locngo.controller;

import com.locngo.dto.CreateLieuImageDto;
import com.locngo.dto.LieuImageByLieuIdDto;
import com.locngo.dto.LieuImageDto;
import com.locngo.services.LieuImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images/lieu")
@PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_USER)")
public class LieuImageController {
    @Autowired
    private LieuImageService lieuImageService;

    @GetMapping("/{lieuId}")
    public LieuImageByLieuIdDto getLieuImageByLieuId(@PathVariable int lieuId) {
        return lieuImageService.findByLieuId(lieuId);
    }

    @PostMapping
    @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_PROPRIETOR)")
    public void addImageToLieu(@RequestBody CreateLieuImageDto createLieuImageDto) {
        lieuImageService.addImageToLieu(createLieuImageDto);
    }

    @PostMapping("/s3/{lieuId}")
    public String getS3BucketName(@RequestParam("file") MultipartFile file, @PathVariable int lieuId) throws Exception {
        return lieuImageService.saveImageOfLieuToS3(file, lieuId);
    }
}
