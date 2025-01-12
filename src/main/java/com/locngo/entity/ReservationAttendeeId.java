package com.locngo.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@Embeddable
public class ReservationAttendeeId implements java.io.Serializable {
    private int reservationId;
    private int attendeeId;

    public ReservationAttendeeId() {}

    public ReservationAttendeeId(int reservationId, int attendeeId) {
        this.reservationId = reservationId;
        this.attendeeId = attendeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationAttendeeId that = (ReservationAttendeeId) o;
        return reservationId == that.reservationId && attendeeId == that.attendeeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, attendeeId);
    }
}
