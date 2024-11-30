package com.locngo.dto;

import java.util.Date;
import java.util.List;

public record ReservationDto(int id, LieuDto lieu, Date start_date, Date end_date, int nb_person, String reference, List<ReservationAttendeeDto> attendees) {
}

