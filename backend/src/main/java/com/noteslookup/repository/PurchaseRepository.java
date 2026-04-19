package com.noteslookup.repository;

import com.noteslookup.model.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

    Page<Purchase> findByBuyer(String buyer, Pageable pageable);

    Page<Purchase> findByItem_ItemId(UUID itemId, Pageable pageable);

    @Query(value = """
            SELECT p.* FROM purchases p
            JOIN items i ON p.item_id = i.item_id
            WHERE i.search_vector @@ plainto_tsquery('english', :query)
               OR i.item_name ILIKE '%' || :query || '%'
            ORDER BY ts_rank(i.search_vector, plainto_tsquery('english', :query)) DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM purchases p
            JOIN items i ON p.item_id = i.item_id
            WHERE i.search_vector @@ plainto_tsquery('english', :query)
               OR i.item_name ILIKE '%' || :query || '%'
            """,
            nativeQuery = true)
    Page<Purchase> searchByItemNotes(@Param("query") String query, Pageable pageable);
}
