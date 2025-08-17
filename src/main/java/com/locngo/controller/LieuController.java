package com.locngo.controller;

import com.locngo.dto.AddServiceToLieuDto;
import com.locngo.dto.AllLieuResponseDto;
import com.locngo.dto.CreateLieuDto;
import com.locngo.dto.LieuDto;
import com.locngo.dto.LieuResponseDto;
import com.locngo.dto.SetLieuFavoritePictureDto;
import com.locngo.services.LieuService;
import java.nio.file.AccessDeniedException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lieu")
public class LieuController {
  @Autowired private LieuService lieuService;

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
  public CreateLieuDto createLieu(
      @RequestBody CreateLieuDto lieuDto,
      @RequestHeader("Authorization") String authorizationHeader) {
    var token = authorizationHeader.substring(7);
    return lieuService.createLieu(lieuDto, token);
  }

  @PostMapping("{id}/addservice")
  @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_PROPRIETOR)")
  public LieuDto addServicesToLieu(
      @PathVariable int id, @RequestBody AddServiceToLieuDto addServiceToLieuDto) {
    return lieuService.addServicesToLieu(id, addServiceToLieuDto.servicesId());
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_PROPRIETOR)")
  public void deleteLieu(@PathVariable int id) {
    lieuService.deleteById(id);
  }

  @PostMapping("/favorite-picture")
  @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_PROPRIETOR)")
  public void setLieuFavoritePicture(
      @RequestBody SetLieuFavoritePictureDto setLieuFavoritePictureDto) {
    lieuService.setLieuFavoritePicture(setLieuFavoritePictureDto);
  }

  @GetMapping("/proprietor/{id}")
  @PreAuthorize("hasRole(T(com.locngo.constants.RoleConstants).ROLE_PROPRIETOR)")
  public List<LieuDto> findByProprietorId(
      @PathVariable int id, @RequestHeader("Authorization") String authorizationHeader)
      throws AccessDeniedException {
    var token = authorizationHeader.substring(7);
    return lieuService.findByProprietorId(id, token);
  }
}
