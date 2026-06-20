package com.aiService.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.aiService.dto.IncidentSummaryResponse;
import com.aiService.entities.IncidentEntity;
import com.aiService.repository.IncidentRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentRepository incidentRepository;

    @GetMapping
    public Page<IncidentSummaryResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        size = Math.min(Math.max(size, 1), 100);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return incidentRepository.findAll(pageable).map(this::toSummary);
    }

    @GetMapping("/{id}")
    public IncidentEntity getById(@PathVariable String id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found: " + id));
    }

    private IncidentSummaryResponse toSummary(IncidentEntity e) {
        return IncidentSummaryResponse.builder()
                .id(e.getId())
                .anomalyId(e.getAnomalyId())
                .hypothesis(e.getHypothesis())
                .confidence(e.getConfidence())
                .affectedComponents(e.getAffectedComponents())
                .suggestedActions(e.getSuggestedActions())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
