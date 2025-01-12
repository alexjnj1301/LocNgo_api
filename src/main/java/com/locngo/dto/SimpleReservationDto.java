package com.locngo.dto;

import java.util.Date;

public record SimpleReservationDto(int id, Date start_date, Date end_date, int nb_person, String reference) {
}
