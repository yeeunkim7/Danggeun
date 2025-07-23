package org.example.danggeun.areaconfirm.repository;

import org.example.danggeun.areaconfirm.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Long> {
    Optional<Area> findTopByUserIdOrderByConfirmedAtDesc(Long userId);
}