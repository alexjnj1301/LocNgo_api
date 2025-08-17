package com.locngo.services;

import com.locngo.entity.Attendees;
import com.locngo.entity.Reservation;
import com.locngo.entity.ReservationAttendee;
import com.locngo.entity.ReservationAttendeeId;
import com.locngo.repository.ReservationAttendeeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationAttendeeService {

  @Autowired private ReservationAttendeeRepository reservationAttendeeRepository;

  public void createMapping(Reservation reservation, Attendees attendee) {
    var reservationAttendee = new ReservationAttendee();
    reservationAttendee.setId(new ReservationAttendeeId(reservation.getId(), attendee.getId()));
    reservationAttendee.setReservation(reservation);
    reservationAttendee.setAttendee(attendee);

    reservationAttendeeRepository.save(reservationAttendee);
  }

  @Transactional
  public void deleteByReservationId(int reservationId) {
    reservationAttendeeRepository.deleteByReservationId(reservationId);
  }

  public void deleteByAttendeeId(int attendeeId) {
    reservationAttendeeRepository.deleteByAttendeeId(attendeeId);
  }

  public void deleteByReservationIdAndAttendeeId(int reservationId, int attendeeId) {
    reservationAttendeeRepository.deleteByReservationIdAndAttendeeId(reservationId, attendeeId);
  }

  @Transactional
  public void deleteAll(List<ReservationAttendee> attendees) {
    if (attendees != null && !attendees.isEmpty()) {
      reservationAttendeeRepository.deleteAll(attendees);
    }
  }
}
