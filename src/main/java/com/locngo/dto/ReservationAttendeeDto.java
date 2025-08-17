package com.locngo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservationAttendeeDto(int attendeeId, String name, String firstname) {}
