package com.locngo.dto;

import java.util.List;

public record ServicesDto(int id, String name, List<LieuServicesDto> lieux) {}
