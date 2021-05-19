package edu.nazarenko.chesser.config;

import edu.nazarenko.chesser.job.AnalyseGameJob;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzSubmitJobs {
    private static final String CRON_EVERY_FIVE_MINUTES = "0 0/5 * ? * * *";

    @Bean(name = "analyseGame")
    public JobDetailFactoryBean jobAnalyseGame() {
        return QuartzConfig.createJobDetail(AnalyseGameJob.class, "Analyse Game Job");
    }

    @Bean(name = "analyseGameTrigger")
    public SimpleTriggerFactoryBean triggerAnalyseGame(@Qualifier("analyseGame") JobDetail jobDetail) {
        return QuartzConfig.createTrigger(jobDetail, 60000, "Analyse Game Trigger");
    }

//    @Bean(name = "memberClassStats")
//    public JobDetailFactoryBean jobMemberClassStats() {
//        return QuartzConfig.createJobDetail(MemberClassStatsJob.class, "Class Statistics Job");
//    }
//
//    @Bean(name = "memberClassStatsTrigger")
//    public CronTriggerFactoryBean triggerMemberClassStats(@Qualifier("memberClassStats") JobDetail jobDetail) {
//        return QuartzConfig.createCronTrigger(jobDetail, CRON_EVERY_FIVE_MINUTES, "Class Statistics Trigger");
//    }
}

