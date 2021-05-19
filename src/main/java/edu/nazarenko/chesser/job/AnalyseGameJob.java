package edu.nazarenko.chesser.job;

import edu.nazarenko.chesser.model.analysis.AnalysisGame;
import edu.nazarenko.chesser.model.game.Game;
import edu.nazarenko.chesser.repository.AnalysisGameRepository;
import edu.nazarenko.chesser.repository.GameRepository;
import edu.nazarenko.chesser.service.AnalysisService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DisallowConcurrentExecution
@AllArgsConstructor
@NoArgsConstructor
public class AnalyseGameJob extends QuartzJobBean {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private AnalysisGameRepository analysisGameRepository;
    @Autowired
    private AnalysisService analysisService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Game game = gameRepository.findFirstByAnalyzed(false);

        if (game != null) {
            AnalysisGame analysisGame = analysisGameRepository.findByGame(game);

            if (analysisGame == null) {
                analysisService.analyseGame(game);
            }
        }
    }
}
