package org.example.danggeun.areaConfirm.repository;

import org.example.danggeun.areaConfirm.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Long> {
    Optional<Area> findTopByUserIdOrderByConfirmedAtDesc(Long userId);
}