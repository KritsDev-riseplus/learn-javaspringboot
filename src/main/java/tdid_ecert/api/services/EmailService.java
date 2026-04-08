package tdid_ecert.api.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    /**
     * Check if email is properly configured
     */
    private boolean isEmailConfigured() {
        return !"your-email@gmail.com".equals(System.getProperty("spring.mail.username", "your-email@gmail.com"))
                && mailSender != null;
    }

    /**
     * Send account notification email
     */
    public void sendAccountNotification(String to, String subject, Map<String, Object> userInfo) {
        try {
            Context context = new Context();
            context.setVariable("subject", subject);
            context.setVariable("greeting", "Dear " + userInfo.getOrDefault("fullName", "User") + ",");
            context.setVariable("message", "Your account has been created successfully. Here are your account details:");
            context.setVariable("userInfo", userInfo);
            context.setVariable("additionalMessage", "Please contact your administrator if you have any questions.");

            String htmlContent = templateEngine.process("email/account-notification", context);
            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.warn("⚠️ Email not sent to {} (SMTP not configured): {}", to, e.getMessage());
        }
    }

    /**
     * Send generic HTML email
     */
    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✅ Email sent successfully to {}", to);
            return true;
        } catch (MessagingException e) {
            log.warn("⚠️ Failed to send email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send custom email from admin to user with formatted template
     */
    public boolean sendCustomEmail(String to, String subject, String recipientName, String content, String senderName) {
        try {
            Context context = new Context();
            context.setVariable("subject", subject);
            context.setVariable("recipientName", recipientName != null ? recipientName : "User");
            context.setVariable("content", content);
            context.setVariable("senderName", senderName != null ? senderName : "Administrator");

            String htmlContent = templateEngine.process("email/custom-email", context);
            return sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.warn("⚠️ Failed to send custom email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send payment confirmation email with inline logo attachment
     */
    public boolean sendPaymentConfirmation(String to, String recipientName, String orderNumber, String continueLink) {
        try {
            Context context = new Context();
            context.setVariable("recipientEmail", to);
            context.setVariable("orderNumber", orderNumber);
            context.setVariable("continueLink", continueLink);
            context.setVariable("recipientInitial", recipientName != null && !recipientName.isEmpty()
                    ? recipientName.substring(0, 1).toUpperCase() : "U");

            String subject = "TDID : แจ้งยืนยันการชำระเงิน และดำเนินการต่อเพื่อสร้างรายการคำขอต่อไป";
            String htmlContent = templateEngine.process("email/payment-confirmation", context);

            // Send with inline logo attachment
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Add logo as inline attachment with cid:logo
            ClassPathResource logoResource = new ClassPathResource("static/images/logo_email.png");
            helper.addInline("logo", logoResource, "image/png");

            mailSender.send(message);
            log.info("✅ Email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send payment confirmation to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send application review notification email
     */
    public boolean sendApplicationReviewNotification(String to, String recipientName, String applicationId, String certType, String companyName, String companyRegNo) {
        try {
            Context context = new Context();
            context.setVariable("recipientEmail", to);
            context.setVariable("applicationId", applicationId);
            context.setVariable("certType", certType);
            context.setVariable("companyName", companyName);
            context.setVariable("companyRegNo", companyRegNo);

            String subject = "TDID : คำขอใหม่รอการตรวจสอบและอนุมัติ – Application ID " + applicationId;
            String htmlContent = templateEngine.process("email/application-review", context);

            // Send with inline logo attachment
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Add logo as inline attachment
            ClassPathResource logoResource = new ClassPathResource("static/images/logo_email.png");
            helper.addInline("logo", logoResource, "image/png");

            mailSender.send(message);
            log.info("✅ Application review email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send application review email to {}: {}", to, e.getMessage());
            return false;
        }
    }
}
