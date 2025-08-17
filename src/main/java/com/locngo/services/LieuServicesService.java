package com.locngo.services;

import com.locngo.entity.Lieu;
import com.locngo.entity.LieuServices;
import com.locngo.entity.LieuServicesId;
import com.locngo.entity.Services;
import com.locngo.repository.LieuServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LieuServicesService {
  @Autowired private LieuServicesRepository lieuServicesRepository;

  public void createMapping(Lieu lieu, Services services) {
    var lieuServices = new LieuServices();
    lieuServices.setId(new LieuServicesId(lieu.getId(), services.getId()));
    lieuServices.setLieu(lieu);
    lieuServices.setServices(services);

    lieuServicesRepository.save(lieuServices);
  }

  public void deleteByLieuId(int lieuId) {
    lieuServicesRepository.deleteByLieuId(lieuId);
  }
}
