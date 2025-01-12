package com.locngo.dto;

import com.locngo.entity.Lieu;
import com.locngo.entity.LieuServicesId;
import com.locngo.entity.Services;

public record LieuServicesDto(LieuServicesId id, Lieu lieu, Services service) {
}
