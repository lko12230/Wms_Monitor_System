package com.wms.monitor.scheduler;

import com.wms.monitor.repository.JobExecutionRepository;
import com.wms.monitor.repository.SchedulerConfigRepository;
import com.wms.monitor.service.DynamicEmailService;
import com.wms.monitor.service.MonitorService;
import com.wms.monitor.service.MonitorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Instant;
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

    // 🔥 Prevent overlapping
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        taskRegistrar.addTriggerTask(

            // ================= JOB =================
            () -> {

                if (!isRunning.compareAndSet(false, true)) {
                    System.out.println("⚠️ Previous job still running, skipping...");
                    return;
                }

                long start = System.currentTimeMillis();

                try {
                    System.out.println("🚀 WMS Monitor Job Started...");

                    // 🔥 IMPORTANT CHANGE
                    MonitorResult result = monitorService.generateExcelReport();

                    // 🔥 PASS COMPLETE DATA
                    emailService.sendReport(
                            result.getFile(),
                            result.isHasMismatch(),
                            result.getMismatchWhList()
                    );

                    long duration = (System.currentTimeMillis() - start) / 1000;

                    String message = result.isHasMismatch()
                            ? "Mismatch found in: " + result.getMismatchWhList()
                            : "No mismatch found";

                    jobRepo.saveLog("SUCCESS", message, duration);

                    System.out.println("✅ Job Completed in " + duration + " sec");

                } catch (Exception e) {

                    long duration = (System.currentTimeMillis() - start) / 1000;

                    jobRepo.saveLog("FAILED", e.getMessage(), duration);

                    System.out.println("❌ Job Failed after " + duration + " sec");
                    e.printStackTrace();

                } finally {
                    isRunning.set(false);
                }
            },

            // ================= CRON =================
            triggerContext -> {

                String cron;

                try {
                    cron = configRepository.getCron();

                    if (cron == null || cron.trim().isEmpty()) {
                        throw new RuntimeException("Empty cron");
                    }

                } catch (Exception e) {
                    System.out.println("⚠️ Using default cron (8 AM)");
                    cron = "0 0 8 * * ?";
                }

                System.out.println("📅 Current Cron: " + cron);

                return new org.springframework.scheduling.support
                        .CronTrigger(cron, ZoneId.of("Asia/Kolkata"))
                        .nextExecution(triggerContext);
            }
        );
    }
}