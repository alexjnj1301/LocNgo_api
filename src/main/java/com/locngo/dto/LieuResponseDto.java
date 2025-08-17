package com.locngo.dto;

import java.util.List;

public record LieuResponseDto(
    int id,
    String name,
    String address,
    String city,
    String postal_code,
    String price,
    String description,
    String favorite_picture,
    List<ReservationDto> reservations,
    List<LieuImageDto> images,
    List<SimpleServiceDto> services) {}
