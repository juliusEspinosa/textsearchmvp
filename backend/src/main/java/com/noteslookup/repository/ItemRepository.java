package com.noteslookup.repository;

import com.noteslookup.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    @Query(value = """
            SELECT * FROM items
            WHERE search_vector @@ plainto_tsquery('english', :query)
               OR item_name ILIKE '%' || :query || '%'
            ORDER BY ts_rank(search_vector, plainto_tsquery('english', :query)) DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM items
            WHERE search_vector @@ plainto_tsquery('english', :query)
               OR item_name ILIKE '%' || :query || '%'
            """,
            nativeQuery = true)
    Page<Item> search(@Param("query") String query, Pageable pageable);
}
