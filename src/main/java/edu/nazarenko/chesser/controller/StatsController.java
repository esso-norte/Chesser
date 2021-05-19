package edu.nazarenko.chesser.controller;

import edu.nazarenko.chesser.controller.dto.StatsDto;
import edu.nazarenko.chesser.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@AllArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    public ResponseEntity<StatsDto> getStats() {
        return ResponseEntity
            .ok()
            .body(statsService.getStats());
    }
}
