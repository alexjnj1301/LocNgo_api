package com.locngo.dto;

import com.locngo.entity.Lieu;
import java.util.Date;

public record AllReservationsByUserIdDto(
    int id,
    String lieuImages,
    Date start_date,
    Date end_date,
    String reference,
    Lieu lieu,
    String status) {}
