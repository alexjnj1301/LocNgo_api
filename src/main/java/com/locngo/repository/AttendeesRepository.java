package com.locngo.repository;

import com.locngo.entity.Attendees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendeesRepository extends JpaRepository<Attendees, Integer> {
  Attendees findById(int id);
}
