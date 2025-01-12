package com.locngo.repository;

import com.locngo.entity.LieuServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LieuServicesRepository extends JpaRepository<LieuServices, Integer> {
    @Modifying
    @Query("DELETE FROM LieuServices ls WHERE ls.services.id = :serviceId")
    void deleteByServiceId(@Param("serviceId") int serviceId);

    @Modifying
    @Query("DELETE FROM LieuServices ls WHERE ls.lieu.id = :lieuId")
    void deleteByLieuId(@Param("lieuId") int lieuId);
}
