package com.locngo.repository;

import com.locngo.dto.UpdateReservation;
import com.locngo.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findAllByLieuId(int lieuId);
    @Query("SELECT reference FROM Reservation WHERE id = ?1")
    String findReferenceByReservationId(int id);
}
