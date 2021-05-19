package edu.nazarenko.chesser.controller.dto;

import edu.nazarenko.chesser.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private boolean challenged;

    public UserDto(User player) {
        this.id = player.getUserId();
        this.username = player.getUsername();
        this.email = player.getEmail();
        this.challenged = false;
    }
}
