package tdid_ecert.api.controllers;

import tdid_ecert.api.dto.CreateUserDTO;
import tdid_ecert.api.dto.SendEmailRequest;
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

    @PostMapping("/{id}/email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send email to user", description = "Send a custom email to a specific user (Admin only)")
    public ResponseEntity<Map<String, String>> sendEmail(@PathVariable Long id, @RequestBody SendEmailRequest request) {
        boolean sent = userService.sendEmailToUser(id, request.getSubject(), request.getContent());
        if (sent) {
            return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
        } else {
            return ResponseEntity.ok(Map.of(
                "message", "Email logged but not sent (SMTP not configured)",
                "info", "Configure SMTP settings in application.properties to enable email sending"
            ));
        }
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
}
