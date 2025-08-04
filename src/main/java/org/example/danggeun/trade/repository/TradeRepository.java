package org.example.danggeun.trade.repository;

import org.example.danggeun.trade.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findByTitle(String title); // 정확히 일치 검색용
}
