package Fream_back.improve_Fream_Back.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentBatchScheduler {

    private final JobLauncher jobLauncher;
    @Qualifier("updateShipmentStatusesJob")
    private final Job updateShipmentStatusesJob;

    @Scheduled(cron = "0 0 */6 * * *")
    public void scheduleShipmentStatusJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(updateShipmentStatusesJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

