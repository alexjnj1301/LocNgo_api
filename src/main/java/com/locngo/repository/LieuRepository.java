package com.locngo.repository;

import com.locngo.entity.Lieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LieuRepository extends JpaRepository<Lieu, Integer> {
    List<Lieu> findByProprietor(Double id);
}
