package com.locngo.dto;

import com.locngo.entity.ReservationAttendee;

import java.util.Date;
import java.util.List;

public record Attendees(int id, String name, String firstname, String email, String phone, Date createdAt, List<ReservationAttendee> reservations) {
}
