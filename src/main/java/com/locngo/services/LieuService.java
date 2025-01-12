package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.AllLieuResponseDto;
import com.locngo.dto.LieuDto;
import com.locngo.dto.LieuImageDto;
import com.locngo.dto.LieuResponseDto;
import com.locngo.dto.LieuServicesDto;
import com.locngo.dto.ReservationDto;
import com.locngo.dto.SimpleServiceDto;
import com.locngo.entity.Lieu;
import com.locngo.exceptions.LieuNotFoundException;
import com.locngo.repository.LieuRepository;
import com.locngo.repository.ServicesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LieuService {
    @Autowired
    private LieuRepository lieuRepository;
    @Autowired
    private LieuServicesService lieuServicesService;
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private LieuImageService lieuImageService;
    private final ObjectMapper objectMapper;

    public LieuService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public LieuResponseDto findById(int id) {
        var lieu = lieuRepository.findById(id)
                .orElseThrow(() -> new LieuNotFoundException("Lieu not found with id " + id));

        List<ReservationDto> reservationsDto = lieu.getReservations().stream()
                .map(reservation -> new ReservationDto(
                        reservation.getId(),
                        null,
                        reservation.getStart_date(),
                        reservation.getEnd_date(),
                        reservation.getNb_person(),
                        reservation.getReference(),
                        null
                ))
                .collect(Collectors.toList());

        List<LieuImageDto> imagesDto = lieu.getImages().stream()
                .map(image -> new LieuImageDto(
                        image.getId(),
                        image.getImageUrl(),
                        null
                ))
                .collect(Collectors.toList());

        List<SimpleServiceDto> servicesDto = lieu.getServices().stream()
                .map(serviceMapping -> new SimpleServiceDto(
                        serviceMapping.getServices().getId(),
                        serviceMapping.getServices().getName()
                ))
                .collect(Collectors.toList());

        return new LieuResponseDto(
                lieu.getId(),
                lieu.getName(),
                lieu.getAddress(),
                lieu.getCity(),
                lieu.getPostal_code(),
                reservationsDto,
                imagesDto,
                servicesDto
        );
    }

    public List<AllLieuResponseDto> findAll() {
        return lieuRepository.findAll().stream()
                .map(lieu -> new AllLieuResponseDto(
                        lieu.getId(),
                        lieu.getName(),
                        lieu.getAddress(),
                        lieu.getCity(),
                        lieu.getPostal_code(),
                        null,
                        this.lieuImageService.findByLieuId(lieu.getId()).getFirst().imageUrl(),
                        null
                ))
                .collect(Collectors.toList());
    }


    public LieuDto createLieu(LieuDto lieuDto) {
        var lieu = new Lieu(lieuDto.id(), lieuDto.name(), lieuDto.address(), lieuDto.city(), lieuDto.postal_code(), null, null, null);
        return this.objectMapper.convertValue(lieuRepository.save(lieu), LieuDto.class);
    }

    public LieuDto addServicesToLieu(int lieuId, List<Integer> servicesId) {
        var lieu = this.lieuRepository.findById(lieuId).orElseThrow(() -> new LieuNotFoundException("Lieu not found"));
        servicesId.forEach(id -> {
            var services = servicesRepository.findById(id).orElseThrow(() -> new RuntimeException("Service not found"));
            this.lieuServicesService.createMapping(lieu, services);
        });
        return this.objectMapper.convertValue(lieuRepository.save(lieu), LieuDto.class);
    }

    @Transactional
    public void deleteById(int lieuId) {
        var lieu = lieuRepository.findById(lieuId)
                .orElseThrow(() -> new RuntimeException("Lieu with id " + lieuId + " not found"));

        lieuServicesService.deleteByLieuId(lieuId);

        reservationService.deleteByLieuId(lieuId);

        lieuImageService.deleteByLieuId(lieuId);

        lieuRepository.delete(lieu);
    }
}
