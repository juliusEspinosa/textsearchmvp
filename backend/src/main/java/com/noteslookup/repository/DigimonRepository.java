package com.noteslookup.repository;

import com.noteslookup.model.Digimon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DigimonRepository extends JpaRepository<Digimon, UUID> {

    @Query(value = """
            SELECT * FROM digimon
            WHERE name_vector @@ plainto_tsquery('english', :query)
               OR name ILIKE '%' || :query || '%'
            ORDER BY ts_rank(name_vector, plainto_tsquery('english', :query)) DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM digimon
            WHERE name_vector @@ plainto_tsquery('english', :query)
               OR name ILIKE '%' || :query || '%'
            """,
            nativeQuery = true)
    Page<Digimon> searchByName(@Param("query") String query, Pageable pageable);

    @Query(value = """
            SELECT * FROM digimon
            WHERE desc_vector @@ plainto_tsquery('english', :query)
               OR description ILIKE '%' || :query || '%'
            ORDER BY ts_rank(desc_vector, plainto_tsquery('english', :query)) DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM digimon
            WHERE desc_vector @@ plainto_tsquery('english', :query)
               OR description ILIKE '%' || :query || '%'
            """,
            nativeQuery = true)
    Page<Digimon> searchByDescription(@Param("query") String query, Pageable pageable);

    @Query(value = """
            SELECT * FROM digimon
            WHERE (name_vector @@ plainto_tsquery('english', :nameQuery)
                   OR name ILIKE '%' || :nameQuery || '%')
              AND (desc_vector @@ plainto_tsquery('english', :descQuery)
                   OR description ILIKE '%' || :descQuery || '%')
            """,
            countQuery = """
            SELECT COUNT(*) FROM digimon
            WHERE (name_vector @@ plainto_tsquery('english', :nameQuery)
                   OR name ILIKE '%' || :nameQuery || '%')
              AND (desc_vector @@ plainto_tsquery('english', :descQuery)
                   OR description ILIKE '%' || :descQuery || '%')
            """,
            nativeQuery = true)
    Page<Digimon> searchByNameAndDescription(@Param("nameQuery") String nameQuery,
                                             @Param("descQuery") String descQuery,
                                             Pageable pageable);

    @Query(value = """
            SELECT * FROM digimon
            WHERE (name_vector @@ plainto_tsquery('english', :nameQuery)
                   OR name ILIKE '%' || :nameQuery || '%')
               OR (desc_vector @@ plainto_tsquery('english', :descQuery)
                   OR description ILIKE '%' || :descQuery || '%')
            """,
            countQuery = """
            SELECT COUNT(*) FROM digimon
            WHERE (name_vector @@ plainto_tsquery('english', :nameQuery)
                   OR name ILIKE '%' || :nameQuery || '%')
               OR (desc_vector @@ plainto_tsquery('english', :descQuery)
                   OR description ILIKE '%' || :descQuery || '%')
            """,
            nativeQuery = true)
    Page<Digimon> searchByNameOrDescription(@Param("nameQuery") String nameQuery,
                                            @Param("descQuery") String descQuery,
                                            Pageable pageable);
}
