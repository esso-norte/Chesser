package edu.nazarenko.chesser.controller;

import edu.nazarenko.chesser.controller.dto.ChallengeAcceptRequest;
import edu.nazarenko.chesser.controller.dto.ChallengeDto;
import edu.nazarenko.chesser.controller.dto.ChallengeRequest;
import edu.nazarenko.chesser.controller.dto.GameDto;
import edu.nazarenko.chesser.service.ChallengeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge")
@AllArgsConstructor
@Slf4j
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public ResponseEntity<List<ChallengeDto>> getAllChallenges() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(challengeService.getAllChallenges());
    }

    @PostMapping("/invite")
    public ResponseEntity<String> invite(@RequestBody ChallengeRequest challengeRequest) {
        challengeService.invite(challengeRequest);
        return new ResponseEntity<>("Player was challenged successfully!", HttpStatus.OK);
    }

    @PostMapping("/accept")
    public ResponseEntity<GameDto> accept(@RequestBody ChallengeAcceptRequest challengeAcceptRequest) {
        return ResponseEntity.ok()
            .body(challengeService.accept(challengeAcceptRequest));
    }
}
