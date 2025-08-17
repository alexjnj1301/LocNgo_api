package com.locngo.repository;

import com.locngo.entity.Lieu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LieuRepository extends JpaRepository<Lieu, Integer> {
  List<Lieu> findByProprietor(Double id);
}
