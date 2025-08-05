package org.example.danggeun.trade.repository;

import org.example.danggeun.trade.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Query("SELECT p FROM Trade p JOIN FETCH p.seller")
    List<Trade> findAllWithSeller();

    Page<Trade> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Optional<Trade> findByTitle(String title);
}
