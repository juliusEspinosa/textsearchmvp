package com.noteslookup.controller.api;

import com.noteslookup.dto.DigimonResponse;
import com.noteslookup.service.DigimonService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/digimon")
public class DigimonController {

    private final DigimonService digimonService;

    public DigimonController(DigimonService digimonService) {
        this.digimonService = digimonService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DigimonResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "and") String mode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(digimonService.search(name, description, mode, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DigimonResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(digimonService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DigimonResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(digimonService.list(page, size));
    }
}
