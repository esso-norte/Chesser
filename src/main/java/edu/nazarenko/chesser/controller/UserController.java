package edu.nazarenko.chesser.controller;

import edu.nazarenko.chesser.controller.dto.UserDto;
import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.service.AuthService;
import edu.nazarenko.chesser.service.UserService;
import edu.nazarenko.chesser.service.game.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        User currentUser = authService.getCurrentUser();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getAllUsers(currentUser));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<GameDto> getGame(@PathVariable Long id) {
//        return ResponseEntity
//            .status(HttpStatus.OK)
//            .body(gameService.getGame(id));
//    }

}
