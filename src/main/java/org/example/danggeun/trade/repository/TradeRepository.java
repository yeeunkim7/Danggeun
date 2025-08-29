package org.example.danggeun.trade.repository;

import org.example.danggeun.trade.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.example.danggeun.user.entity.User;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Query(
            value = """
            SELECT * 
            FROM product p
            WHERE 
                (p.title ILIKE '%' || :keyword || '%' 
                 OR p.product_detail ILIKE '%' || :keyword || '%')
            ORDER BY 
                CASE 
                    WHEN p.title ILIKE :keyword || '%' THEN 1
                    WHEN p.title ILIKE '%' || :keyword || '%' THEN 2
                    ELSE 3
                END,
                p.product_created_at DESC
        """,
            countQuery = """
            SELECT COUNT(*) 
            FROM product p
            WHERE 
                (p.title ILIKE '%' || :keyword || '%'
                 OR p.product_detail ILIKE '%' || :keyword || '%')
        """,
            nativeQuery = true
    )
    Page<Trade> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
        SELECT DISTINCT t.title
        FROM Trade t
        WHERE LOWER(t.title) LIKE CONCAT(LOWER(:prefix), '%')
        ORDER BY t.views DESC
    """)
    List<String> findTitlesByPrefix(@Param("prefix") String prefix, Pageable pageable);

    @Query("""
        SELECT t FROM Trade t
        JOIN FETCH t.seller
    """)
    List<Trade> findAllWithSeller();

    Optional<Trade> findFirstBySellerAndTitle(User seller, String title);
}
