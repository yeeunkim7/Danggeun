package org.example.danggeun.trade.repository;

import org.example.danggeun.trade.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(
            value = """
            SELECT i.* 
            FROM item i
            WHERE i.status = 'SALE'
              AND (
                    i.title   ILIKE '%' || :keyword || '%'
                 OR i.content ILIKE '%' || :keyword || '%'
              )
            ORDER BY 
              CASE 
                WHEN i.title ILIKE :keyword || '%'       THEN 1
                WHEN i.title ILIKE '%' || :keyword || '%' THEN 2
                ELSE 3
              END,
              i.created_at DESC
        """,
            countQuery = """
            SELECT COUNT(*) 
            FROM item i
            WHERE i.status = 'SALE'
              AND (
                    i.title   ILIKE '%' || :keyword || '%'
                 OR i.content ILIKE '%' || :keyword || '%'
              )
        """,
            nativeQuery = true
    )
    Page<Item> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(
            "SELECT DISTINCT i.title " +
                    "FROM Item i " +
                    "WHERE i.status = 'SALE' " +
                    "  AND LOWER(i.title) LIKE CONCAT(LOWER(:prefix), '%') " +
                    "ORDER BY i.viewCount DESC"
    )
    List<String> findTitlesByPrefix(@Param("prefix") String prefix, Pageable pageable);
}
