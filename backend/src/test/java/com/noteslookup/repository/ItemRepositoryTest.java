package com.noteslookup.repository;

import com.noteslookup.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("itemsearch_test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init-test.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void save_givenValidItem_persistsAndGeneratesId() {
        // Given
        var item = new Item("Test Item", "Some test notes");

        // When
        var saved = itemRepository.save(item);

        // Then
        assertThat(saved.getItemId()).isNotNull();
        assertThat(saved.getItemName()).isEqualTo("Test Item");
        assertThat(saved.getNotes()).isEqualTo("Some test notes");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void findById_givenExistingItem_returnsItem() {
        // Given
        var item = itemRepository.save(new Item("Find Me", "Searchable notes"));

        // When
        var found = itemRepository.findById(item.getItemId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getItemName()).isEqualTo("Find Me");
    }

    @Test
    void findById_givenNonExistingId_returnsEmpty() {
        // When
        var found = itemRepository.findById(java.util.UUID.randomUUID());

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void delete_givenExistingItem_removesFromDatabase() {
        // Given
        var item = itemRepository.save(new Item("Delete Me", "To be deleted"));
        var id = item.getItemId();

        // When
        itemRepository.deleteById(id);

        // Then
        assertThat(itemRepository.findById(id)).isEmpty();
    }

    @Test
    void search_givenMatchingQuery_returnsResults() {
        // Given
        itemRepository.save(new Item("Wireless Mouse", "Ergonomic bluetooth mouse with silent clicks"));
        itemRepository.save(new Item("USB Keyboard", "Mechanical keyboard with cherry switches"));

        // When
        var results = itemRepository.search("mouse", PageRequest.of(0, 20));

        // Then
        assertThat(results.getContent()).isNotEmpty();
        assertThat(results.getContent()).allSatisfy(item ->
                assertThat(item.getItemName().toLowerCase() + " " + item.getNotes().toLowerCase())
                        .containsIgnoringCase("mouse"));
    }

    @Test
    void search_givenNoMatch_returnsEmpty() {
        // Given
        itemRepository.save(new Item("Wireless Mouse", "Ergonomic mouse"));

        // When
        var results = itemRepository.search("xyznonexistent", PageRequest.of(0, 20));

        // Then
        assertThat(results.getContent()).isEmpty();
    }

    @Test
    void search_givenPartialWord_returnsResults() {
        // Given
        itemRepository.save(new Item("Ergonomic Keyboard", "Split keyboard design"));

        // When
        var results = itemRepository.search("ergo", PageRequest.of(0, 20));

        // Then
        assertThat(results.getContent()).isNotEmpty();
    }

    @Test
    void findAll_givenMultipleItems_returnsAll() {
        // Given
        long initialCount = itemRepository.count();
        itemRepository.save(new Item("Item A", "Notes A"));
        itemRepository.save(new Item("Item B", "Notes B"));

        // When
        var all = itemRepository.findAll();

        // Then
        assertThat(all).hasSizeGreaterThanOrEqualTo((int) initialCount + 2);
    }
}
