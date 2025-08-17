package com.locngo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.CreateServicesDto;
import com.locngo.dto.ServicesDto;
import com.locngo.entity.Services;
import com.locngo.exceptions.ServiceNotFoundException;
import com.locngo.repository.LieuServicesRepository;
import com.locngo.repository.ServicesRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicesService {
  @Autowired ServicesRepository servicesRepository;
  @Autowired LieuServicesRepository lieuServicesRepository;
  private final ObjectMapper objectMapper;

  public ServicesService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public ServicesDto findById(int id) {
    var service =
        servicesRepository
            .findById(id)
            .orElseThrow(
                () -> new ServiceNotFoundException("Service with id " + id + " not found"));
    return this.objectMapper.convertValue(service, ServicesDto.class);
  }

  public List<ServicesDto> findAll() {
    return this.servicesRepository.findAll().stream()
        .map(services -> this.objectMapper.convertValue(services, ServicesDto.class))
        .collect(Collectors.toList());
  }

  public ServicesDto createService(CreateServicesDto servicesDto) {
    return this.objectMapper.convertValue(
        servicesRepository.save(this.objectMapper.convertValue(servicesDto, Services.class)),
        ServicesDto.class);
  }

  @Transactional
  public void deleteById(int serviceId) {
    var service =
        servicesRepository
            .findById(serviceId)
            .orElseThrow(
                () -> new ServiceNotFoundException("Service with id " + serviceId + " not found"));

    lieuServicesRepository.deleteByServiceId(serviceId);

    servicesRepository.delete(service);
  }
}
