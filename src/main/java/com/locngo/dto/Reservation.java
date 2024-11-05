package com.locngo.dto;

import com.locngo.entity.Lieu;
import com.locngo.entity.ReservationAttendee;

import java.util.Date;
import java.util.List;

public record Reservation(int id, Lieu lieu, Date start_date, Date end_date, int nb_person, List<ReservationAttendee> attendees) {
}

