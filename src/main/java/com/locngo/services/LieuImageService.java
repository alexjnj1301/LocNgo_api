package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.CreateLieuImageDto;
import com.locngo.dto.LieuDto;
import com.locngo.dto.LieuImageDto;
import com.locngo.entity.Lieu;
import com.locngo.entity.LieuImage;
import com.locngo.repository.LieuImageRepository;
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
    private final ObjectMapper objectMapper;
    @Value("${links.replacement}") private String replacementImageLink;

    public LieuImageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<LieuImageDto> findByLieuId(int lieuId) {
        var listLieuImage = this.lieuImageRepository.findByLieuId(lieuId)
                .stream()
                .map(lieuImage -> new LieuImageDto(lieuImage.getId(), lieuImage.getImageUrl(), this.objectMapper.convertValue(lieuImage.getLieu(), LieuDto.class)))
                .collect(Collectors.toList());

        return listLieuImage.isEmpty() ? List.of(new LieuImageDto(0, replacementImageLink, null)) : listLieuImage;
    }

    public void addImageToLieu(CreateLieuImageDto createLieuImageDto) {
        var lieuImage = new LieuImage(createLieuImageDto.id(), createLieuImageDto.url(), createLieuImageDto.lieu());
        this.lieuImageRepository.save(lieuImage);
    }

    public void deleteByLieuId(int lieuId) {
        this.lieuImageRepository.deleteByLieuId(lieuId);
    }
}
