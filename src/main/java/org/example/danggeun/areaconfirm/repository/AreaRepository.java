package org.example.danggeun.areaconfirm.repository;

import org.example.danggeun.areaconfirm.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    Optional<Area> findTopByUserIdOrderByConfirmedAtDesc(Long userId);

    List<Area> findAllByUserIdOrderByConfirmedAtDesc(Long userId);

    @Query("SELECT COUNT(a) > 0 FROM Area a WHERE a.userId = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}