package com.locngo.repository;

import com.locngo.entity.ReservationAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationAttendeeRepository extends JpaRepository<ReservationAttendee, Integer> {
  void deleteByReservationId(int reservationId);

  void deleteByAttendeeId(int attendeeId);

  void deleteByReservationIdAndAttendeeId(int reservationId, int attendeeId);
}
