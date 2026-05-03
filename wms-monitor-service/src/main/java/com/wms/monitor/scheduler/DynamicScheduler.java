package com.wms.monitor.scheduler;

import com.wms.monitor.repository.JobExecutionRepository;
import com.wms.monitor.repository.SchedulerConfigRepository;
import com.wms.monitor.service.DynamicEmailService;
import com.wms.monitor.service.MonitorService;
import com.wms.monitor.service.MonitorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@EnableScheduling
public class DynamicScheduler implements org.springframework.scheduling.annotation.SchedulingConfigurer {

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private DynamicEmailService emailService;

    @Autowired
    private SchedulerConfigRepository configRepository;

    @Autowired
    private JobExecutionRepository jobRepo;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        taskRegistrar.addTriggerTask(

            () -> {

                if (!isRunning.compareAndSet(false, true)) {
                    System.out.println("⚠️ Previous job still running...");
                    return;
                }

                long start = System.currentTimeMillis();

                try {
                    System.out.println("🚀 Job Started...");

                    MonitorResult result = monitorService.generateExcelReport();

                    long duration = (System.currentTimeMillis() - start) / 1000;

                    String message = result.isHasMismatch()
                            ? "Mismatch found in: " + result.getMismatchWhList()
                            : "No mismatch found";

                    // 🔥 SAVE + GET ID
                    long jobId = jobRepo.saveLog("SUCCESS", message, duration);

                    // 🔥 PASS ID TO EMAIL
                    emailService.sendReport(
                            result.getFile(),
                            result.isHasMismatch(),
                            result.getMismatchWhList(),
                            result.getDateTime(),
                            jobId
                    );

                    System.out.println("✅ Job Completed ID: " + jobId);

                } catch (Exception e) {

                    long duration = (System.currentTimeMillis() - start) / 1000;
                    jobRepo.saveLog("FAILED", e.getMessage(), duration);

                    System.out.println("❌ Job Failed");
                    e.printStackTrace();

                } finally {
                    isRunning.set(false);
                }
            },

            triggerContext -> {
                String cron;
                try {
                    cron = configRepository.getCron();
                } catch (Exception e) {
                    cron = "0 */3 * * * ?"; // every 3 min fallback
                }

                return new org.springframework.scheduling.support
                        .CronTrigger(cron, ZoneId.of("Asia/Kolkata"))
                        .nextExecution(triggerContext);
            }
        );
    }
}