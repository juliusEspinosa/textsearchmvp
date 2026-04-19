package com.noteslookup.repository;

import com.noteslookup.model.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PokemonRepository extends JpaRepository<Pokemon, UUID> {

    @Query(value = """
            SELECT * FROM pokemon
            WHERE search_vector @@ plainto_tsquery('english', :query)
               OR name ILIKE '%' || :query || '%'
            ORDER BY ts_rank(search_vector, plainto_tsquery('english', :query)) DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM pokemon
            WHERE search_vector @@ plainto_tsquery('english', :query)
               OR name ILIKE '%' || :query || '%'
            """,
            nativeQuery = true)
    Page<Pokemon> search(@Param("query") String query, Pageable pageable);
}
