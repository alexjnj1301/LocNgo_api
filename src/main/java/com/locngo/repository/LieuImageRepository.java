package com.locngo.repository;

import com.locngo.entity.LieuImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LieuImageRepository extends JpaRepository<LieuImage, Integer> {
  List<LieuImage> findByLieuId(int lieuId);

  @Modifying
  @Query("DELETE FROM LieuImage li WHERE li.lieu.id = :lieuId")
  void deleteByLieuId(@Param("lieuId") int lieuId);
}
