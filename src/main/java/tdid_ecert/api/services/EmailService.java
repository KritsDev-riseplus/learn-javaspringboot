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

    /**
     * Send application confirmation email (waiting for signature)
     */
    public boolean sendApplicationConfirmationEmail(String to, String recipientName, String applicationId, String certType, String companyName, String companyRegNo, String verifyLink) {
        try {
            Context context = new Context();
            context.setVariable("recipientEmail", to);
            context.setVariable("recipientName", recipientName);
            context.setVariable("applicationId", applicationId);
            context.setVariable("certType", certType);
            context.setVariable("companyName", companyName);
            context.setVariable("companyRegNo", companyRegNo);
            context.setVariable("verifyLink", verifyLink);

            String subject = "TDID : ยืนยันตัวตนและลงนามคำขอใบรับรองอิเล็กทรอนิกส์แบบออนไลน์ (รอลงนาม) – Application ID " + applicationId;
            String htmlContent = templateEngine.process("email/application-confirmation", context);

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
            log.info("✅ Application confirmation email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send application confirmation email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send signature status notification email
     */
    public boolean sendSignatureStatusNotification(String to, String recipientEmail, String applicationId, String certType, String companyName, String companyRegNo, String signatureStatus, java.util.List<String> signers) {
        try {
            Context context = new Context();
            context.setVariable("recipientEmail", recipientEmail);
            context.setVariable("applicationId", applicationId);
            context.setVariable("certType", certType);
            context.setVariable("companyName", companyName);
            context.setVariable("companyRegNo", companyRegNo);
            context.setVariable("signatureStatus", signatureStatus);
            context.setVariable("signers", signers);

            String subject = "TDID : แจ้งสถานะการลงนามขอใบรับรองอิเล็กทรอนิกส์ – Application ID " + applicationId;
            String htmlContent = templateEngine.process("email/signature-status-notification", context);

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
            log.info("✅ Signature status notification email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send signature status notification to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send signing completed notification email
     */
    public boolean sendSigningCompletedEmail(String to, String recipientEmail, String recipientName, String applicationId, String certType, String companyName, String companyRegNo) {
        try {
            Context context = new Context();
            context.setVariable("recipientEmail", recipientEmail);
            context.setVariable("recipientName", recipientName);
            context.setVariable("applicationId", applicationId);
            context.setVariable("certType", certType);
            context.setVariable("companyName", companyName);
            context.setVariable("companyRegNo", companyRegNo);

            String subject = "TDID : ยืนยันตัวตนและลงนามคำขอใบรับรองอิเล็กทรอนิกส์แบบออนไลน์ (ลงนามสำเร็จ) – Application ID " + applicationId;
            String htmlContent = templateEngine.process("email/signing-completed", context);

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
            log.info("✅ Signing completed email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send signing completed email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send certificate completion email (ใบรับรองอิเล็กทรอนิกส์ เสร็จสิ้น)
     * Format: TDID : รายการคำขอใบรับรองอิเล็กทรอนิกส์ (เสร็จสิ้น)
     */
    public boolean sendCertificateCompletionEmail(String to, String recipientEmail, String recipientName, String applicationId, String certType, String companyName, String companyRegNo, String authCode, String validFrom, String validTo, String loginLink) {
        try {
            Context context = new Context();
            context.setVariable("recipientEmail", recipientEmail);
            context.setVariable("recipientName", recipientName);
            context.setVariable("applicationId", applicationId);
            context.setVariable("certType", certType);
            context.setVariable("companyName", companyName);
            context.setVariable("companyRegNo", companyRegNo);
            context.setVariable("authCode", authCode);
            context.setVariable("validFrom", validFrom);
            context.setVariable("validTo", validTo);
            context.setVariable("loginLink", loginLink);

            String subject = "TDID : รายการคำขอใบรับรองอิเล็กทรอนิกส์ (เสร็จสิ้น) – Application ID " + applicationId;
            String htmlContent = templateEngine.process("email/certificate-completion", context);

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
            log.info("✅ Certificate completion email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send certificate completion email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send certificate rejection email (ใบรับรองอิเล็กทรอนิกส์ ไม่อนุมัติ)
     * Format: TDID : รายการคำขอใบรับรองอิเล็กทรอนิกส์ (ไม่อนุมัติ)
     */
    public boolean sendCertificateRejectionEmail(String to, String recipientEmail, String recipientName, String applicationId, String certType, String companyName, String companyRegNo, String rejectionReason, String fixLink) {
        try {
            Context context = new Context();
            context.setVariable("recipientEmail", recipientEmail);
            context.setVariable("recipientName", recipientName);
            context.setVariable("applicationId", applicationId);
            context.setVariable("certType", certType);
            context.setVariable("companyName", companyName);
            context.setVariable("companyRegNo", companyRegNo);
            context.setVariable("rejectionReason", rejectionReason);
            context.setVariable("fixLink", fixLink);

            String subject = "TDID : รายการคำขอใบรับรองอิเล็กทรอนิกส์ (ไม่อนุมัติ) – Application ID " + applicationId;
            String htmlContent = templateEngine.process("email/certificate-rejection", context);

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
            log.info("✅ Certificate rejection email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send certificate rejection email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send certificate download notification email (มอบใบรับรองอิเล็กทรอนิกส์)
     */
    public boolean sendCertificateDownloadEmail(String to, String recipientEmail, String recipientName, String applicationId, String serialNumber, String status, String statusColor, String validFrom, String validTo, String downloadLink) {
        try {
            Context context = new Context();
            context.setVariable("recipientEmail", recipientEmail);
            context.setVariable("recipientName", recipientName);
            context.setVariable("applicationId", applicationId);
            context.setVariable("serialNumber", serialNumber);
            context.setVariable("status", status);
            context.setVariable("statusColor", statusColor);
            context.setVariable("validFrom", validFrom);
            context.setVariable("validTo", validTo);
            context.setVariable("downloadLink", downloadLink);

            String subject = "TDID CA : ดาวน์โหลดใบรับรองอิเล็กทรอนิกส์ – Application ID " + applicationId;
            String htmlContent = templateEngine.process("email/certificate-download", context);

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
            log.info("✅ Certificate download email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send certificate download email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send new admin account notification email
     */
    public boolean sendNewAdminEmail(String to, String username, String password, String activationLink) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("password", password);
            context.setVariable("activationLink", activationLink);

            String subject = "TDID : ข้อมูลผู้ใช้งานและรหัสผ่านใหม่ของคุณ";
            String htmlContent = templateEngine.process("email/new-admin", context);

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
            log.info("✅ New admin email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send new admin email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send password reset notification email
     */
    public boolean sendNewPasswordEmail(String to, String newPassword, String loginUrl) {
        try {
            Context context = new Context();
            context.setVariable("newPassword", newPassword);
            context.setVariable("loginUrl", loginUrl);

            String subject = "TDID : รหัสผ่านใหม่ของคุณ";
            String htmlContent = templateEngine.process("email/new-password", context);

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
            log.info("✅ New password email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send new password email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send OTP verification email
     */
    public boolean sendOtpEmail(String to, String otp, String refCode) {
        try {
            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("refCode", refCode);

            String subject = "TDID : Verify Email OTP";
            String htmlContent = templateEngine.process("email/sent-otp", context);

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
            log.info("✅ OTP email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send OTP email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send welcome email with Application ID and validity period
     */
    public boolean sendWelcomeEmail(String to, String applicationId, int validDays, String validFrom, String validTo, String loginUrl) {
        try {
            Context context = new Context();
            context.setVariable("applicationId", applicationId);
            context.setVariable("validDays", validDays);
            context.setVariable("validFrom", validFrom);
            context.setVariable("validTo", validTo);
            context.setVariable("loginUrl", loginUrl);

            String subject = "TDID : ยินดีต้อนรับเข้าสู่ระบบใบรับรองอิเล็กทรอนิกส์";
            String htmlContent = templateEngine.process("email/welcome-email", context);

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
            log.info("✅ Welcome email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Failed to send welcome email to {}: {}", to, e.getMessage());
            return false;
        }
    }
}
