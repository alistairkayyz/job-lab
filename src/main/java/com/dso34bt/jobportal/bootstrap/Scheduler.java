package com.dso34bt.jobportal.bootstrap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class Scheduler {

    @Scheduled(fixedRate = 1000)
    public void cronJobSch() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        if (now.before(Timestamp.valueOf("2022-01-20 22:43:00")) )
            System.out.println("Java cron job expression:: " + now);
    }
}
