package com.locngo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.locngo.entity.Lieu;
import com.locngo.entity.LieuServices;
import com.locngo.entity.LieuServicesId;
import com.locngo.entity.Services;
import com.locngo.repository.LieuServicesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LieuServicesServiceTest {

  @Mock private LieuServicesRepository lieuServicesRepository;

  @InjectMocks private LieuServicesService lieuServicesService;

  private Lieu lieu;
  private Services services;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Création d'un lieu et d'un service fictifs
    lieu = new Lieu();
    lieu.setId(1);

    services = new Services();
    services.setId(2);
  }

  @Test
  void testCreateMapping_success() {
    // Act
    lieuServicesService.createMapping(lieu, services);

    // Assert
    ArgumentCaptor<LieuServices> captor = ArgumentCaptor.forClass(LieuServices.class);
    verify(lieuServicesRepository, times(1)).save(captor.capture());

    LieuServices savedMapping = captor.getValue();
    assertEquals(lieu.getId(), savedMapping.getLieu().getId());
    assertEquals(services.getId(), savedMapping.getServices().getId());
    assertEquals(new LieuServicesId(lieu.getId(), services.getId()), savedMapping.getId());
  }

  @Test
  void testDeleteByLieuId_success() {
    int lieuId = 1;

    // Act
    lieuServicesService.deleteByLieuId(lieuId);

    // Assert
    verify(lieuServicesRepository, times(1)).deleteByLieuId(lieuId);
  }
}
