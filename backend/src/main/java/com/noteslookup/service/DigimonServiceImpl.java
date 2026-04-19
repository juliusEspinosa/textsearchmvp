package com.noteslookup.service;

import com.noteslookup.config.DigimonNotFoundException;
import com.noteslookup.dto.DigimonResponse;
import com.noteslookup.repository.DigimonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DigimonServiceImpl implements DigimonService {

    private final DigimonRepository digimonRepository;

    public DigimonServiceImpl(DigimonRepository digimonRepository) {
        this.digimonRepository = digimonRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DigimonResponse> list(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return digimonRepository.findAll(pageable).map(DigimonResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DigimonResponse> search(String name, String description, String mode, int page, int size) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasDesc = description != null && !description.isBlank();

        if (!hasName && !hasDesc) {
            return list(page, size);
        }

        var pageable = PageRequest.of(page, size);

        if (hasName && hasDesc) {
            return "and".equalsIgnoreCase(mode)
                    ? digimonRepository.searchByNameAndDescription(name.trim(), description.trim(), pageable).map(DigimonResponse::from)
                    : digimonRepository.searchByNameOrDescription(name.trim(), description.trim(), pageable).map(DigimonResponse::from);
        }

        if (hasName) {
            return digimonRepository.searchByName(name.trim(), pageable).map(DigimonResponse::from);
        }

        return digimonRepository.searchByDescription(description.trim(), pageable).map(DigimonResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public DigimonResponse getById(UUID id) {
        var digimon = digimonRepository.findById(id)
                .orElseThrow(() -> new DigimonNotFoundException(id));
        return DigimonResponse.from(digimon);
    }
}
