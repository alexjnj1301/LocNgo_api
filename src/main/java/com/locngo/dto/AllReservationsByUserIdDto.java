package com.locngo.dto;

import java.util.Date;
import java.util.List;

public record AllReservationsByUserIdDto(int id, List<String> lieuImages, Date start_date, Date end_date, String reference) {
}
