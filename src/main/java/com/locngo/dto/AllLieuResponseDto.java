package com.locngo.dto;

import java.util.List;

public record AllLieuResponseDto(int id, String name, String address, String city, String postal_code,
                                 List<ReservationDto> reservations, String favorite_picture, List<SimpleServiceDto> services) {
}
