package com.noteslookup.service;

import com.noteslookup.dto.DigimonResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface DigimonService {

    Page<DigimonResponse> list(int page, int size);

    Page<DigimonResponse> search(String name, String description, String mode, int page, int size);

    DigimonResponse getById(UUID id);
}
