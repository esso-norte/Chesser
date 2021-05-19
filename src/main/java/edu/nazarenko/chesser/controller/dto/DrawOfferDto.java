package edu.nazarenko.chesser.controller.dto;

import edu.nazarenko.chesser.model.game.DrawOffer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DrawOfferDto {

    private boolean isEmpty;
    private Long id;
    private GameDto gameDto;
    private UserDto player;

    public DrawOfferDto() {
        this.isEmpty = true;
    }

    public DrawOfferDto(DrawOffer drawOffer) {

        if (drawOffer == null) {
            this.isEmpty = true;
        } else {
            this.isEmpty = false;
            this.id = drawOffer.getId();
            this.gameDto = new GameDto(drawOffer.getGame());
            this.player = new UserDto(drawOffer.getPlayer());
        }
    }
}
