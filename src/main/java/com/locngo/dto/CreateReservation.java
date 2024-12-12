package com.locngo.dto;

import com.locngo.entity.Lieu;
import com.locngo.entity.User;

import java.util.Date;
import java.util.List;

public record CreateReservation(int id, Lieu lieu, Date start_date, Date end_date, int nb_person, String reference, List<AttendeeDto> attendees) {
}

