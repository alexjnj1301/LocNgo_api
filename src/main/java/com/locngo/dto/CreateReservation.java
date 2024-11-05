package com.locngo.dto;

import com.locngo.entity.Lieu;

import java.util.Date;
import java.util.List;

public record CreateReservation(Lieu lieu, Date start_date, Date end_date, int nb_person, List<Integer> attendees_id) {
}

