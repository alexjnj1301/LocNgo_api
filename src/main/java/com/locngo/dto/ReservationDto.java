package com.locngo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservationDto(
    int id,
    LieuDto lieu,
    Date start_date,
    Date end_date,
    int nb_person,
    String reference,
    String status,
    List<ReservationAttendeeDto> attendees) {}
