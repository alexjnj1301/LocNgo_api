package com.locngo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ReservationAttendee {
  @EmbeddedId private ReservationAttendeeId id;

  @ManyToOne
  @MapsId("reservationId")
  @JsonIgnoreProperties("attendees")
  private Reservation reservation;

  @ManyToOne
  @MapsId("attendeeId")
  @JsonIgnoreProperties({"reservations", "attendees"})
  private Attendees attendee;
}
