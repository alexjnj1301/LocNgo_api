package com.locngo.dto;

import com.locngo.entity.Attendees;
import com.locngo.entity.Lieu;
import com.locngo.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

public record UpdateReservation(int id, Lieu lieu, User user, Date start_date, Date end_date, int nb_person, String status, List<Attendees> attendees) {
}

