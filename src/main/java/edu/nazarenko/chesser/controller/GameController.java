package edu.nazarenko.chesser.controller;

import edu.nazarenko.chesser.controller.dto.*;
import edu.nazarenko.chesser.exceptions.ChesserException;
import edu.nazarenko.chesser.model.game.Game;
import edu.nazarenko.chesser.service.game.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@AllArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameDto gameDto) {
        return ResponseEntity
            .ok()
            .body(gameService.save(gameDto));
    }

    @GetMapping
    public ResponseEntity<List<GameDto>> getAllGames() {
        return ResponseEntity
            .ok()
            .body(gameService.getGamesOfCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getGame(@PathVariable Long id) {
        return ResponseEntity
            .ok()
            .body(gameService.getGame(id));
    }

    @PostMapping("/move")
    public ResponseEntity<MakeMoveResponse> makeMove(@RequestBody MoveRequest moveRequest) {
        MakeMoveResponse responseBody = gameService.tryToMakeMove(moveRequest);

        return ResponseEntity
            .status(responseBody.isValid() ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE)
            .body(responseBody);
    }

    @GetMapping("/{id}/draw-offer")
    public ResponseEntity<DrawOfferDto> getDrawOffer(@PathVariable Long id) {
        return ResponseEntity
            .ok()
            .body(gameService.getDrawOffer(id));
    }

    @PostMapping("/offer-draw")
    public ResponseEntity<DrawOfferDto> offerDraw(@RequestBody GameDto gameDto) {
        return ResponseEntity
            .ok()
            .body(gameService.offerDraw(gameDto));
    }

    @PostMapping("/decline-draw")
    public ResponseEntity<DrawOfferDto> declineDraw(@RequestBody GameDto gameDto) {
        return ResponseEntity
            .ok()
            .body(gameService.declineDraw(gameDto));
    }

    @PostMapping("/resign")
    public ResponseEntity<String> resign(@RequestBody GameDto gameDto) {
        boolean success = gameService.resign(gameDto);

        return ResponseEntity
            .status(success ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE)
            .body(success ? "Resigned successfully" : "Could not resign");
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyze(@RequestBody GameDto gameDto) {
        boolean success = gameService.analyse(gameDto);

        return ResponseEntity
            .status(success ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE)
            .body(success ? "Analysis started" : "ERROR");
    }
}
