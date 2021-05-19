package edu.nazarenko.chesser.service.game;

import edu.nazarenko.chesser.controller.dto.*;
import edu.nazarenko.chesser.exceptions.ChesserException;
import edu.nazarenko.chesser.job.AnalyseGameJob;
import edu.nazarenko.chesser.mapper.GameMapper;
import edu.nazarenko.chesser.model.User;
import edu.nazarenko.chesser.model.analysis.AnalysisGame;
import edu.nazarenko.chesser.model.game.DrawOffer;
import edu.nazarenko.chesser.model.game.Game;
import edu.nazarenko.chesser.model.game.GameResult;
import edu.nazarenko.chesser.model.game.GameTermination;
import edu.nazarenko.chesser.repository.AnalysisGameRepository;
import edu.nazarenko.chesser.repository.DrawOfferRepository;
import edu.nazarenko.chesser.repository.GameRepository;
import edu.nazarenko.chesser.repository.UserRepository;
import edu.nazarenko.chesser.service.AuthService;
import edu.nazarenko.chesser.service.StatsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final DrawOfferRepository drawOfferRepository;
    private final AnalysisGameRepository analysisGameRepository;

    private final AuthService authService;
    private final GameMapper gameMapper;
    private final PGNService pgnService;
    private final FENService fenService;
    private final StatsService statsService;

    @Transactional
    public GameDto save(GameDto gameDto) {
        User whitePlayer = userRepository.findByUsername("user1").orElseThrow(() -> new ChesserException("No such player"));
        User blackPlayer = userRepository.findByUsername("user2").orElseThrow(() -> new ChesserException("No such player"));
        gameDto.setWhitePlayer(whitePlayer);
        gameDto.setBlackPlayer(blackPlayer);
        gameDto.setCreated(Instant.now());
        gameDto.setPgn(pgnService.generatePgnFromGameDto(gameDto));
        gameDto.setFen(fenService.startingPosition());

        Game save = gameRepository.save(gameMapper.mapDtoToGame(gameDto));
        gameDto.setId(save.getId());
        return gameDto;
    }

    @Transactional(readOnly = true)
    public List<GameDto> getGamesOfCurrentUser() {
        User user = authService.getCurrentUser();

        return gameRepository.findByWhitePlayerOrBlackPlayer(user, user)
            .stream()
            .sorted((Game game1, Game game2) -> game1.isFinished() == game2.isFinished() ?
                game2.getCreated().compareTo(game1.getCreated()) :
                Boolean.compare(game1.isFinished(), game2.isFinished()))
            .map(gameMapper::mapGameToDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GameDto getGame(Long id) {
        Game game = gameRepository
            .findById(id)
            .orElseThrow(() -> new ChesserException("No game found with id: " + id));
        return gameMapper.mapGameToDto(game);
    }

    // MOVE LOGIC
    @Transactional
    public MakeMoveResponse tryToMakeMove(MoveRequest moveRequest) {
        try {
            User player = authService.getCurrentUser();
            Game game = gameRepository
                .findById(moveRequest.getGameId())
                .orElseThrow(() -> new ChesserException("No game found with id: " + moveRequest.getGameId()));

            if (game.isFinished()) {
                return MakeMoveResponse.builder()
                    .isValid(false)
                    .build();
            }

            FenDto fenDto = new FenDto(game.getFen());
            Color color = fenDto.getActiveColor();
            Board board = new Board(fenDto, game.getPgn());
            Square from = board.getSquare(moveRequest.getFrom());
            Square to = board.getSquare(moveRequest.getTo());
            String promotionNotation = moveRequest.getPromotion();

            Move move = new Move(board, from, to, promotionNotation);

            boolean isValid = isPlayersMove(game, player) && move.isLegal();

            if (isValid) {
                makeMove(move);
                game.setFen(board.getFenDto().toString());
                game.setPgn(board.getPgn());

                if (board.isMate(color.opposite())) {
                    finishGame(game, GameResult.fromWinner(color), GameTermination.MATE);
                } else if (board.isStalemate(color.opposite())) {
                    finishGame(game, GameResult.DRAW, GameTermination.STALEMATE);
                } else if (board.getFenDto().getHalfMoveClock() == 100) {
                    finishGame(game, GameResult.DRAW, GameTermination.FIFTY_MOVES);
                } else if (board.insufficientMaterial()) {
                    finishGame(game, GameResult.DRAW, GameTermination.INSUFFICIENT_MATERIAL);
                }

                gameRepository.save(game);
            }

            return MakeMoveResponse.builder()
                .isValid(isValid)
                .build();
        } catch (ChesserException e) {
            e.printStackTrace();
            return MakeMoveResponse.builder()
                .isValid(false)
                .build();
        }
    }

    private boolean isPlayersMove(Game game, User player) {
        String fen = game.getFen();
        char toMove = fen.charAt(fen.indexOf(" ") + 1);

        return (toMove == 'w' && game.getWhitePlayer().getUserId().equals(player.getUserId())) ||
            (toMove == 'b' && game.getBlackPlayer().getUserId().equals(player.getUserId()));
    }

    @Transactional
    protected void makeMove(Move move) {
        Board board = move.getBoard();
        Color color = board.getFenDto().getActiveColor();
        int moveNo = board.getFenDto().getMoveClock();
        String moveNotation = move.makeMove();

        String pgn = board.getPgn();
        pgn = pgnService.addMove(pgn, color, moveNo, moveNotation);

        board.setPgn(pgn);
    }
    // END MOVE LOGIC

    // FINISH GAME LOGIC
    @Transactional
    protected void finishGame(Game game, GameResult result, GameTermination termination) {
        game.setFinished(true);
        game.setResult(result);
        game.setTermination(termination);
        game.setPgn(pgnService.addResult(game.getPgn(), game.getResult(), termination));

        if (result.equals(GameResult.DRAW)) {
            statsService.addDraw(game.getWhitePlayer(), game.getBlackPlayer());
        } else {
            User winner = result.equals(GameResult.WHITE_WIN) ? game.getWhitePlayer() : game.getBlackPlayer();
            User looser = result.equals(GameResult.BLACK_WIN) ? game.getWhitePlayer() : game.getBlackPlayer();

            statsService.addWin(winner, looser);
        }
    }
    // END FINISH GAME LOGIC

    // DRAW AND RESIGN LOGIC
    @Transactional(readOnly = true)
    public DrawOfferDto getDrawOffer(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new ChesserException("No game found with id: " + gameId));
        DrawOffer drawOffer = drawOfferRepository.findByGame(game).orElse(null);
        return new DrawOfferDto(drawOffer);
    }

    @Transactional
    public DrawOfferDto offerDraw(GameDto gameDto) {
        User user = authService.getCurrentUser();
        Game game = gameRepository.findById(gameDto.getId()).orElseThrow(() -> new ChesserException("No game with id: " + gameDto.getId()));

        if (
            game == null ||
            (!game.getWhitePlayer().getUserId().equals(user.getUserId()) &&
                !game.getBlackPlayer().getUserId().equals(user.getUserId()))
        ) {
            throw new ChesserException("No Access");
        }

        DrawOffer drawOffer = drawOfferRepository.findByGame(game).orElse(null);

        if (drawOffer == null) { // Offer
            drawOffer = DrawOffer.builder()
                .game(game)
                .player(user)
                .build();
            drawOfferRepository.save(drawOffer);

            return new DrawOfferDto(drawOffer);

        } else { // Accept
            drawOfferRepository.delete(drawOffer);
            finishGame(game, GameResult.DRAW, GameTermination.DRAW_AGREEMENT);

            return new DrawOfferDto();
        }
    }

    @Transactional
    public DrawOfferDto declineDraw(GameDto gameDto) {
        User user = authService.getCurrentUser();
        Game game = gameRepository.findById(gameDto.getId()).orElseThrow(() -> new ChesserException("No game with id: " + gameDto.getId()));

        if (
            game == null ||
                (!game.getWhitePlayer().getUserId().equals(user.getUserId()) &&
                    !game.getBlackPlayer().getUserId().equals(user.getUserId()))
        ) {
            throw new ChesserException("No Access");
        }

        DrawOffer drawOffer = drawOfferRepository.findByGame(game)
            .orElseThrow(() -> new ChesserException("No draw offer for game with id: " + gameDto.getId()));

        if (drawOffer.getPlayer().getUserId().equals(user.getUserId())) {
            throw new ChesserException("You are not recipient of a draw offer");
        }

        drawOfferRepository.delete(drawOffer);
        return new DrawOfferDto();
    }

    @Transactional
    public boolean resign(GameDto gameDto) {
        User user = authService.getCurrentUser();
        Game game = gameRepository.findById(gameDto.getId()).orElseThrow(() -> new ChesserException("No game with id: " + gameDto.getId()));

        if (
            game == null ||
            game.isFinished() ||
            (!game.getWhitePlayer().getUserId().equals(user.getUserId()) &&
                !game.getBlackPlayer().getUserId().equals(user.getUserId()))
        ) {
            return false;
        }

        Color color = game.getWhitePlayer().getUserId().equals(user.getUserId()) ? Color.WHITE : Color.BLACK;
        finishGame(game, GameResult.fromWinner(color.opposite()), GameTermination.RESIGNATION);

        return true;
    }
    // END DRAW AND RESIGN LOGIC

    @Transactional
    public boolean analyse(GameDto gameDto) {
        Game game = gameRepository.findById(gameDto.getId())
            .orElseThrow(() -> new ChesserException("No game with id: " + gameDto.getId()));

        if (game.isAnalyzed()) {
            return false;
        }

        AnalysisGame analysisGame = analysisGameRepository.findByGame(game);

        if (analysisGame == null) {
            return false;
        }

        analysisGame = AnalysisGame.builder()
            .game(game)
            .finished(false)
            .build();

        analysisGameRepository.save(analysisGame);

        return true;
    }
}
