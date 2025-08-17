package com.locngo.repository;

import com.locngo.entity.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  List<Reservation> findAllByLieuId(int lieuId);

  List<Reservation> findByUserId(int userId);

  List<Reservation> findByUserIdOrderByIdDesc(int userId);
}
