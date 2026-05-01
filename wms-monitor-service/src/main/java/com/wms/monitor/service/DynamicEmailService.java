package com.wms.monitor.service;

import com.wms.monitor.repository.EmailConfigRepository;
import com.wms.monitor.repository.MailConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

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

    // 🔥 UPDATED METHOD SIGNATURE
    public void sendReport(File file, boolean hasMismatch, String mismatchWh) {

        long start = System.currentTimeMillis();

        try {
            // ================= FETCH EMAILS =================
            List<String> emails = emailRepo.getEmails();

            if (emails == null || emails.isEmpty()) {
                System.out.println("⚠️ No recipients found, skipping email");
                return;
            }

            // ================= FETCH CONFIG =================
            Map<String, String> config = mailConfigRepo.getAllConfigs();

            if (config == null || config.isEmpty()) {
                System.out.println("❌ Mail config missing in DB");
                return;
            }

            // ================= CONFIG =================
            String host = config.getOrDefault("MAIL_HOST", "smtp.gmail.com");
            int port = Integer.parseInt(config.getOrDefault("MAIL_PORT", "587"));
            String username = config.get("MAIL_USERNAME");
            String password = config.get("MAIL_PASSWORD");
            String from = config.getOrDefault("MAIL_FROM", username);

            if (username == null || password == null) {
                System.out.println("❌ Username/Password missing in DB");
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

            helper.setFrom(from);
            helper.setTo(emails.toArray(new String[0]));
            helper.setSubject("WMS Daily Monitoring Report | REDTAG");

            // ================= STATUS =================
            String statusHtml;

            if (hasMismatch) {
                statusHtml = "<span style='color:red; font-weight:bold;'>❌ Mismatch Found in: "
                        + mismatchWh + "</span>";
            } else {
                statusHtml = "<span style='color:green; font-weight:bold;'>✅ Completed - No Mismatch Found</span>";
            }

            // ================= HTML BODY =================
            String body = """
                <html>
                <body style="margin:0; padding:0; background:#f4f6f8; font-family:Arial;">

                <table width="100%" style="padding:20px;">
                <tr><td align="center">

                <table width="650" style="background:white; border-radius:8px; overflow:hidden;">

                <!-- HEADER -->
                <tr>
                <td style="background:#0a2a43; padding:20px; text-align:center;">

                    <img src="cid:logoImage"
                         style="width:60px; height:auto; display:block; margin:0 auto 10px auto;"
                         alt="Company Logo"/>

                    <p style="color:white; margin-top:10px;">
                        Warehouse Monitoring System
                    </p>

                </td>
                </tr>

                <!-- BODY -->
                <tr>
                <td style="padding:30px;">

                <h2 style="color:#0a2a43;">WMS Daily Monitoring Report</h2>

                <p>Dear Team,</p>

                <p>
                Please find attached <b>WMS Monitoring Report</b> for <b>REDTAG</b>.
                </p>

                <table width="100%" style="border-collapse:collapse;">
                    <tr>
                        <td style="padding:10px; border:1px solid #ddd;"><b>Client</b></td>
                        <td style="padding:10px; border:1px solid #ddd;">REDTAG</td>
                    </tr>
                    <tr>
                        <td style="padding:10px; border:1px solid #ddd;"><b>Implemented By</b></td>
                        <td style="padding:10px; border:1px solid #ddd;">Trangile Services</td>
                    </tr>
                    <tr>
                        <td style="padding:10px; border:1px solid #ddd;"><b>Status</b></td>
                        <td style="padding:10px; border:1px solid #ddd;">
                            """ + statusHtml + """
                        </td>
                    </tr>
                </table>

                <p style="margin-top:20px;">
                This is an automated monitoring report.
                </p>

                <p>
                Regards,<br>
                <b>WMS Monitoring System</b>
                </p>

                </td>
                </tr>

                <!-- FOOTER -->
                <tr>
                <td style="background:#f1f1f1; padding:15px; text-align:center; font-size:12px;">
                Auto generated email - Do not reply
                </td>
                </tr>

                </table>

                </td></tr>
                </table>

                </body>
                </html>
                """;

            helper.setText(body, true);

            // ================= INLINE LOGO =================
            ClassPathResource logo = new ClassPathResource("static/logo.jpg");
            helper.addInline("logoImage", logo);

            // ================= ATTACH FILE =================
            if (file != null && file.exists()) {
                helper.addAttachment("WMS_Report.xlsx", new FileSystemResource(file));
            }

            // ================= RETRY =================
            int retry = 0;

            while (retry < 3) {
                try {
                    mailSender.send(message);

                    long duration = (System.currentTimeMillis() - start) / 1000;
                    System.out.println("📧 Email sent successfully in " + duration + " sec");

                    return;

                } catch (Exception e) {
                    retry++;
                    System.out.println("⚠️ Email failed, retry " + retry);

                    if (retry == 3) {
                        System.out.println("❌ Email failed after 3 attempts");
                        e.printStackTrace();
                    }

                    Thread.sleep(3000);
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Email Service Failed Completely");
            e.printStackTrace();
        }
    }
}