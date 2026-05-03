package com.wms.monitor.service;

import com.wms.monitor.repository.EmailConfigRepository;
import com.wms.monitor.repository.MailConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class DynamicEmailService {

    @Autowired
    private EmailConfigRepository emailRepo;

    @Autowired
    private MailConfigRepository mailConfigRepo;

    // ================= MAIN =================
    public void sendReport(File file, boolean hasMismatch, String mismatchWh, String dateTime, long jobId) {
        sendInternal(file, hasMismatch, mismatchWh, dateTime, jobId);
    }

    // ================= BACKWARD =================
    public void sendReport(File file, boolean hasMismatch, String mismatchWh, String dateTime) {
        sendInternal(file, hasMismatch, mismatchWh, dateTime, -1);
    }

    // ================= CORE =================
    private void sendInternal(File file, boolean hasMismatch, String mismatchWh, String dateTime, long jobId) {

        long start = System.currentTimeMillis();

        try {
            // ================= EMAIL LIST =================
            List<String> emails = emailRepo.getEmails();
            if (emails == null || emails.isEmpty()) {
                System.out.println("⚠️ No recipients found");
                return;
            }

            // ================= CONFIG =================
            Map<String, String> config = mailConfigRepo.getAllConfigs();
            if (config == null || config.isEmpty()) {
                System.out.println("❌ Mail config missing");
                return;
            }

            String host = config.getOrDefault("MAIL_HOST", "smtp.gmail.com");
            int port = Integer.parseInt(config.getOrDefault("MAIL_PORT", "587"));
            String username = config.get("MAIL_USERNAME");
            String password = config.get("MAIL_PASSWORD");

            if (username == null || password == null) {
                System.out.println("❌ Username/Password missing");
                return;
            }

            // ================= MAIL SENDER =================
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(host);
            mailSender.setPort(port);
            mailSender.setUsername(username);
            mailSender.setPassword(password);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.connectiontimeout", "20000");
            props.put("mail.smtp.timeout", "20000");
            props.put("mail.smtp.writetimeout", "20000");

            // ================= MESSAGE =================
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(username, "Trangile Automated WMS"));
            helper.setTo(emails.toArray(new String[0]));
            helper.setSubject("📊 WMS Daily Monitoring Report | REDTAG");

            // ================= HTML TEMPLATE =================
            String body = """
            <html>
            <body style="margin:0; background:#eef2f7; font-family:Segoe UI,Roboto,Arial;">

            <table width="100%" cellpadding="0" cellspacing="0" style="padding:30px;">
            <tr><td align="center">

            <table width="650" style="background:#ffffff; border-radius:14px; overflow:hidden; box-shadow:0 8px 30px rgba(0,0,0,0.08);">

            <!-- HEADER -->
            <tr>
            <td style="background:linear-gradient(135deg,#0a2a43,#0f4c75); padding:30px; text-align:center;">
                <img src="cid:logoImage" style="width:60px; margin-bottom:10px;" />
                <h2 style="color:white; margin:0;">📊 WMS Monitoring Report</h2>
            </td>
            </tr>

            <!-- BODY -->
            <tr>
            <td style="padding:30px;">

            <p>Dear Team,</p>
            <p>Please find attached the latest <b>WMS Monitoring Report</b>.</p>

            <!-- STATUS CARD -->
            <div style="background:{{BG_COLOR}}; padding:15px; border-radius:8px; margin-bottom:20px;">

                <span style="display:inline-block; padding:6px 12px; border-radius:20px;
                font-weight:bold; color:{{TEXT_COLOR}}; background:{{PILL_BG}};">
                {{STATUS_TEXT}}
                </span>

                <div style="margin-top:8px; font-size:13px;">
                {{DESC}}
                </div>
            </div>

            <!-- INFO TABLE -->
            <table width="100%" style="border:1px solid #ddd;">
                <tr>
                    <td style="padding:10px;"><b>📅 Generated On</b></td>
                    <td style="padding:10px;">{{DATE_TIME}}</td>
                </tr>
                <tr>
                    <td style="padding:10px;"><b>🆔 Job ID</b></td>
                    <td style="padding:10px;">{{JOB_ID}}</td>
                </tr>
                <tr>
                    <td style="padding:10px;"><b>🏢 Client</b></td>
                    <td style="padding:10px;">REDTAG</td>
                </tr>
            </table>

            <div style="margin-top:20px; padding:10px; background:#fff3cd; border-radius:6px;">
            ⚠️ Please review highlighted <b style="color:red;">RED</b> rows in the report.
            </div>

            <p style="margin-top:20px;">
            Regards,<br>
            <b>Trangile Automated WMS</b>
            </p>

            </td>
            </tr>

            <!-- FOOTER -->
            <tr>
            <td style="background:#f1f3f6; padding:15px; text-align:center; font-size:12px;">
            Auto-generated email • Do not reply
            </td>
            </tr>

            </table>

            </td></tr>
            </table>

            </body>
            </html>
            """;

            // ================= REPLACE VALUES =================
            String bgColor = hasMismatch ? "#fdecea" : "#e6f4ea";
            String textColor = hasMismatch ? "#d93025" : "#188038";
            String pillBg = hasMismatch ? "#fce8e6" : "#dff6e3";
            String statusText = hasMismatch ? "❌ Issues Found" : "✅ All Systems Normal";
            String desc = hasMismatch
                    ? "Affected Warehouses: <b>" + mismatchWh + "</b>"
                    : "No mismatches detected";

            body = body.replace("{{BG_COLOR}}", bgColor)
                       .replace("{{TEXT_COLOR}}", textColor)
                       .replace("{{PILL_BG}}", pillBg)
                       .replace("{{STATUS_TEXT}}", statusText)
                       .replace("{{DESC}}", desc)
                       .replace("{{DATE_TIME}}", dateTime)
                       .replace("{{JOB_ID}}", jobId == -1 ? "N/A" : String.valueOf(jobId));

            helper.setText(body, true);

            // ================= LOGO =================
            ClassPathResource logo = new ClassPathResource("static/logo.jpg");
            helper.addInline("logoImage", logo);

            // ================= ATTACHMENT =================
            if (file != null && file.exists()) {
                helper.addAttachment("WMS_Report.xlsx", new FileSystemResource(file));
            }

            // ================= SEND =================
            mailSender.send(message);

            long duration = (System.currentTimeMillis() - start) / 1000;
            System.out.println("📧 Email sent in " + duration + " sec");

        } catch (Exception e) {
            System.out.println("❌ Email failed");
            e.printStackTrace();
        } finally {
            // 🔥 DELETE FILE AFTER SEND
            if (file != null && file.exists()) {
                boolean deleted = file.delete();
                System.out.println("🗑 File deleted: " + deleted);
            }
        }
    }
}