package com.locngo.dto;

import java.util.List;

public record AttendeeDto(int id, String name, String firstname, List<SimpleReservationDto> reservations) {
}
