package com.locngo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.CreateServicesDto;
import com.locngo.dto.ServicesDto;
import com.locngo.entity.Services;
import com.locngo.exceptions.ServiceNotFoundException;
import com.locngo.repository.LieuServicesRepository;
import com.locngo.repository.ServicesRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ServicesServiceTest {

  @Mock private ServicesRepository servicesRepository;

  @Mock private LieuServicesRepository lieuServicesRepository;

  @InjectMocks private ServicesService servicesService;

  private Services service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ObjectMapper objectMapper = new ObjectMapper();
    servicesService = new ServicesService(objectMapper);
    servicesService.servicesRepository = servicesRepository;
    servicesService.lieuServicesRepository = lieuServicesRepository;

    service = new Services();
    service.setId(1);
    service.setName("WiFi");
  }

  @Test
  void testFindById_success() {
    when(servicesRepository.findById(1)).thenReturn(Optional.of(service));

    ServicesDto dto = servicesService.findById(1);

    assertNotNull(dto);
    assertEquals(service.getId(), dto.id());
    assertEquals(service.getName(), dto.name());
    verify(servicesRepository, times(1)).findById(1);
  }

  @Test
  void testFindById_notFound() {
    when(servicesRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(ServiceNotFoundException.class, () -> servicesService.findById(99));
    verify(servicesRepository, times(1)).findById(99);
  }

  @Test
  void testFindAll_success() {
    when(servicesRepository.findAll()).thenReturn(Collections.singletonList(service));

    List<ServicesDto> result = servicesService.findAll();

    assertEquals(1, result.size());
    assertEquals(service.getName(), result.getFirst().name());
    verify(servicesRepository, times(1)).findAll();
  }

  @Test
  void testCreateService_success() {
    CreateServicesDto dto = new CreateServicesDto("WiFi");
    when(servicesRepository.save(any(Services.class))).thenReturn(service);

    ServicesDto result = servicesService.createService(dto);

    assertNotNull(result);
    assertEquals("WiFi", result.name());
    verify(servicesRepository, times(1)).save(any(Services.class));
  }

  @Test
  void testDeleteById_success() {
    when(servicesRepository.findById(1)).thenReturn(Optional.of(service));

    servicesService.deleteById(1);

    verify(lieuServicesRepository, times(1)).deleteByServiceId(1);
    verify(servicesRepository, times(1)).delete(service);
  }

  @Test
  void testDeleteById_notFound() {
    when(servicesRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(ServiceNotFoundException.class, () -> servicesService.deleteById(99));
    verify(servicesRepository, never()).delete(any());
    verify(lieuServicesRepository, never()).deleteByServiceId(anyInt());
  }
}
