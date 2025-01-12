package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.CreateLieuImageDto;
import com.locngo.dto.LieuDto;
import com.locngo.dto.LieuImageByIdDto;
import com.locngo.dto.LieuImageByLieuIdDto;
import com.locngo.dto.LieuImageDto;
import com.locngo.entity.Lieu;
import com.locngo.entity.LieuImage;
import com.locngo.exceptions.ImageNotFoundException;
import com.locngo.repository.LieuImageRepository;
import com.locngo.repository.LieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LieuImageService {
    @Autowired
    private LieuImageRepository lieuImageRepository;

    public LieuImageByLieuIdDto findByLieuId(int lieuId) {
        var listUrl = new ArrayList<>();
        this.lieuImageRepository.findByLieuId(lieuId).forEach(lieuImage -> listUrl.add(lieuImage.getImageUrl()));

        return new LieuImageByLieuIdDto(listUrl, lieuId);
    }

    public void addImageToLieu(CreateLieuImageDto createLieuImageDto) {
        var lieuImage = new LieuImage(createLieuImageDto.id(), createLieuImageDto.url(), createLieuImageDto.lieu());
        this.lieuImageRepository.save(lieuImage);
    }

    public void deleteByLieuId(int lieuId) {
        this.lieuImageRepository.deleteByLieuId(lieuId);
    }

    public LieuImageByIdDto getById(int imageId) {
        var lieuImage = this.lieuImageRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException("Image not found with id " + imageId));
        return new LieuImageByIdDto(lieuImage.getId(), lieuImage.getImageUrl(), lieuImage.getLieu().getId());
    }
}
