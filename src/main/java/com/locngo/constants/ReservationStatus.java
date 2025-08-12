package com.locngo.constants;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING(),
    CONFIRMED(),
    CANCELED();
}
