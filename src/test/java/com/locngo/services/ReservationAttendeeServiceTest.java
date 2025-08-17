package com.locngo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.locngo.entity.Attendees;
import com.locngo.entity.Reservation;
import com.locngo.entity.ReservationAttendee;
import com.locngo.entity.ReservationAttendeeId;
import com.locngo.repository.ReservationAttendeeRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ReservationAttendeeServiceTest {

  @Mock private ReservationAttendeeRepository reservationAttendeeRepository;

  @InjectMocks private ReservationAttendeeService reservationAttendeeService;

  private Reservation reservation;
  private Attendees attendee;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    reservation = new Reservation();
    reservation.setId(1);

    attendee = new Attendees();
    attendee.setId(2);
  }

  @Test
  void testCreateMapping_success() {
    // Act
    reservationAttendeeService.createMapping(reservation, attendee);

    // Assert
    ArgumentCaptor<ReservationAttendee> captor = ArgumentCaptor.forClass(ReservationAttendee.class);
    verify(reservationAttendeeRepository, times(1)).save(captor.capture());

    ReservationAttendee saved = captor.getValue();
    assertEquals(reservation.getId(), saved.getReservation().getId());
    assertEquals(attendee.getId(), saved.getAttendee().getId());
    assertEquals(new ReservationAttendeeId(reservation.getId(), attendee.getId()), saved.getId());
  }

  @Test
  void testDeleteByReservationId_success() {
    int reservationId = 1;

    reservationAttendeeService.deleteByReservationId(reservationId);

    verify(reservationAttendeeRepository, times(1)).deleteByReservationId(reservationId);
  }

  @Test
  void testDeleteByAttendeeId_success() {
    int attendeeId = 2;

    reservationAttendeeService.deleteByAttendeeId(attendeeId);

    verify(reservationAttendeeRepository, times(1)).deleteByAttendeeId(attendeeId);
  }

  @Test
  void testDeleteByReservationIdAndAttendeeId_success() {
    int reservationId = 1;
    int attendeeId = 2;

    reservationAttendeeService.deleteByReservationIdAndAttendeeId(reservationId, attendeeId);

    verify(reservationAttendeeRepository, times(1))
        .deleteByReservationIdAndAttendeeId(reservationId, attendeeId);
  }

  @Test
  void testDeleteAll_success() {
    ReservationAttendee ra1 = new ReservationAttendee();
    ReservationAttendee ra2 = new ReservationAttendee();

    List<ReservationAttendee> list = List.of(ra1, ra2);

    reservationAttendeeService.deleteAll(list);

    verify(reservationAttendeeRepository, times(1)).deleteAll(list);
  }

  @Test
  void testDeleteAll_emptyOrNull() {
    reservationAttendeeService.deleteAll(null);
    reservationAttendeeService.deleteAll(List.of());

    verify(reservationAttendeeRepository, never()).deleteAll(any());
  }
}
