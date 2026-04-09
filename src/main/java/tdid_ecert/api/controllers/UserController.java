package tdid_ecert.api.controllers;

import tdid_ecert.api.dto.CreateUserDTO;
import tdid_ecert.api.dto.UpdateUserDTO;
import tdid_ecert.api.dto.UserDTO;
import tdid_ecert.api.services.UserService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "Admin only endpoints for managing users")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve list of all non-admin users (Admin only)")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieve a single user by ID (Admin only)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user", description = "Create a new user account (Admin only)")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserDTO request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Update an existing user (Admin only)")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete a user by ID (Admin only)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/payment-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send payment confirmation email", description = "Send auto-generated payment confirmation email (Admin only)")
    public ResponseEntity<Map<String, String>> sendPaymentEmail(@PathVariable Long id) {
        // Generate order number and link
        String orderNumber = "TDID" + System.currentTimeMillis();
        String continueLink = "https://yourdomain/continue?token=" + java.util.UUID.randomUUID().toString();

        boolean sent = userService.sendPaymentConfirmationToUser(id, orderNumber, continueLink);
        if (sent) {
            return ResponseEntity.ok(Map.of(
                "message", "Payment confirmation email sent successfully",
                "orderNumber", orderNumber
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending",
                "orderNumber", orderNumber
            ));
        }
    }

    @PostMapping("/{id}/application-review-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send application review email", description = "Send notification about new application for review (Admin only)")
    public ResponseEntity<Map<String, String>> sendApplicationReviewEmail(@PathVariable Long id) {
        boolean sent = userService.sendApplicationReviewNotificationToUser(id);
        if (sent) {
            return ResponseEntity.ok(Map.of("message", "Application review email sent successfully"));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending"
            ));
        }
    }

    @PostMapping("/{id}/application-confirmation-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send application confirmation email", description = "Send confirmation email for application signing (Admin only)")
    public ResponseEntity<Map<String, String>> sendApplicationConfirmationEmail(@PathVariable Long id) {
        boolean sent = userService.sendApplicationConfirmationToUser(id);
        if (sent) {
            return ResponseEntity.ok(Map.of("message", "Application confirmation email sent successfully"));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending"
            ));
        }
    }

    @PostMapping("/{id}/signature-status-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send signature status notification email", description = "Send notification about signature signing status (Admin only)")
    public ResponseEntity<Map<String, String>> sendSignatureStatusEmail(@PathVariable Long id) {
        boolean sent = userService.sendSignatureStatusNotificationToUser(id);
        if (sent) {
            return ResponseEntity.ok(Map.of("message", "Signature status notification email sent successfully"));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending"
            ));
        }
    }

    @PostMapping("/{id}/signing-completed-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send signing completed notification email", description = "Send notification that signing is completed (Admin only)")
    public ResponseEntity<Map<String, String>> sendSigningCompletedEmail(@PathVariable Long id) {
        // Generate mock application data
        String applicationId = "NEW-06-O" + System.currentTimeMillis() % 1000 + "-3-1-25-" + (id + 5000000);
        String certType = "นิติบุคคล (Enterprise Certificate)";
        String companyName = "บริษัท ตัวอย่าง จำกัด";
        String companyRegNo = "0105543" + (id + 1000);

        boolean sent = userService.sendSigningCompletedNotificationToUser(id, applicationId, certType, companyName, companyRegNo);
        if (sent) {
            return ResponseEntity.ok(Map.of("message", "Signing completed email sent successfully"));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending"
            ));
        }
    }

    @PostMapping("/{id}/cert-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send certificate completion email", description = "Send certificate completion email with auth code, validity dates and login link (Admin only)")
    public ResponseEntity<Map<String, String>> sendCertificateEmail(@PathVariable Long id) {
        // Generate mock application data matching the email format
        String applicationId = "NEW-06-O191-3-1-25-" + (5000000 + id);
        String certType = "นิติบุคคล (Enterprise Certificate)";
        String companyName = "บริษัท ไทยดิจิทัล ไอดี จำกัด";
        String companyRegNo = "0105543112679";
        String authCode = "TD" + System.currentTimeMillis() % 1000000000L + "DMNK" + (2000 + id);

        // Valid for 60 days from now
        java.time.LocalDate validFrom = java.time.LocalDate.now();
        java.time.LocalDate validTo = validFrom.plusDays(60);
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.ENGLISH);

        String loginLink = "https://thaidigitalid.com/e-cert/auth/login";

        boolean sent = userService.sendCertificateCompletionToUser(
                id, applicationId, certType, companyName, companyRegNo,
                authCode, validFrom.format(dateFormatter), validTo.format(dateFormatter), loginLink
        );

        if (sent) {
            return ResponseEntity.ok(Map.of(
                "message", "ใบรับรองอิเล็กทรอนิกส์ส่งสำเร็จ!",
                "applicationId", applicationId,
                "authCode", authCode
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending",
                "applicationId", applicationId,
                "authCode", authCode
            ));
        }
    }

    @PostMapping("/{id}/cert-rejection-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send certificate rejection email", description = "Send certificate rejection email with rejection reason and fix link (Admin only)")
    public ResponseEntity<Map<String, String>> sendCertificateRejectionEmail(@PathVariable Long id) {
        // Generate mock application data matching the rejection email format
        String applicationId = "NEW-06-O191-3-1-25-" + (5000000 + id);
        String certType = "นิติบุคคล (Enterprise Certificate)";
        String companyName = "บริษัท ไทยดิจิทัล ไอดี จำกัด";
        String companyRegNo = "0105543112679";
        String rejectionReason = "<เหตุผล Reject จาก Web RA>";
        String fixLink = "https://thaidigitalid.com/e-cert/enterprise/reject/application-info";

        boolean sent = userService.sendCertificateRejectionToUser(
                id, applicationId, certType, companyName, companyRegNo,
                rejectionReason, fixLink
        );

        if (sent) {
            return ResponseEntity.ok(Map.of(
                "message", "อีเมลไม่อนุมัติใบรับรองอิเล็กทรอนิกส์ส่งสำเร็จ!",
                "applicationId", applicationId
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending",
                "applicationId", applicationId
            ));
        }
    }

    @PostMapping("/{id}/cert-download-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send certificate download notification email", description = "Send certificate download email with serial number, status, and download link (Admin only)")
    public ResponseEntity<Map<String, String>> sendCertificateDownloadEmail(@PathVariable Long id) {
        // Generate mock application data matching the download email format
        String applicationId = "NEW-06-O191-3-1-25-" + (5000000 + id);
        String serialNumber = "4A3B2C1D0E" + (5000000 + id) + "F6A7B8C9D";
        String status = "Active";
        String statusColor = "color: green; font-weight: 600";

        // Valid for 1 year from now
        java.time.LocalDate validFrom = java.time.LocalDate.now();
        java.time.LocalDate validTo = validFrom.plusYears(1);
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.ENGLISH);

        String downloadLink = "https://thaidigitalid.com/e-cert/download/" + applicationId;

        boolean sent = userService.sendCertificateDownloadToUser(
                id, applicationId, serialNumber, status, statusColor,
                validFrom.format(dateFormatter), validTo.format(dateFormatter), downloadLink
        );

        if (sent) {
            return ResponseEntity.ok(Map.of(
                "message", "อีเมลดาวน์โหลดใบรับรองอิเล็กทรอนิกส์ส่งสำเร็จ!",
                "applicationId", applicationId,
                "serialNumber", serialNumber
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending",
                "applicationId", applicationId
            ));
        }
    }

    @PostMapping("/{id}/new-admin-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send new admin account notification email", description = "Send new admin account credentials and activation link (Admin only)")
    public ResponseEntity<Map<String, String>> sendNewAdminEmail(@PathVariable Long id) {
        // Generate mock account data
        String username = "admin" + id;
        String password = "Temp@" + System.currentTimeMillis() % 10000;
        String activationLink = "https://thaidigitalid.com/activate?token=" + java.util.UUID.randomUUID();

        boolean sent = userService.sendNewAdminToUser(id, username, password, activationLink);

        if (sent) {
            return ResponseEntity.ok(Map.of(
                "message", "อีเมลข้อมูลผู้ใช้งานใหม่ส่งสำเร็จ!",
                "username", username
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending",
                "username", username
            ));
        }
    }

    @PostMapping("/{id}/new-password-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send password reset notification email", description = "Send new password and login link to user (Admin only)")
    public ResponseEntity<Map<String, String>> sendNewPasswordEmail(@PathVariable Long id) {
        // Generate mock password data
        String newPassword = "Reset@" + System.currentTimeMillis() % 10000;
        String loginUrl = "https://thaidigitalid.com/login";

        boolean sent = userService.sendNewPasswordToUser(id, newPassword, loginUrl);

        if (sent) {
            return ResponseEntity.ok(Map.of(
                "message", "อีเมลรหัสผ่านใหม่ส่งสำเร็จ!",
                "loginUrl", loginUrl
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending",
                "loginUrl", loginUrl
            ));
        }
    }
}
