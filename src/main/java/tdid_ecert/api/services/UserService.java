package tdid_ecert.api.services;

import tdid_ecert.api.dto.CreateUserDTO;
import tdid_ecert.api.dto.UpdateUserDTO;
import tdid_ecert.api.dto.UserDTO;
import tdid_ecert.api.entities.Role;
import tdid_ecert.api.entities.User;
import tdid_ecert.api.repositories.RoleRepository;
import tdid_ecert.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;

@Service
@RequiredArgsConstructor
@Tag(name = "User Management Service", description = "Business logic for user operations")
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Get the currently authenticated user's username
     */
    private String getCurrentUsername() {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return null;
    }

    /**
     * Check if the user being modified is an admin
     */
    private boolean isAdminUser(User user) {
        return "ADMIN".equals(user.getRole().getName());
    }

    @Operation(summary = "Get all users", description = "Retrieve all non-admin users from database")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !"ADMIN".equals(user.getRole().getName()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Send payment confirmation email", description = "Send auto-generated payment confirmation email to a user")
    public boolean sendPaymentConfirmationToUser(Long userId, String orderNumber, String continueLink) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return emailService.sendPaymentConfirmation(
                user.getEmail(),
                user.getFullName(),
                orderNumber,
                continueLink
        );
    }

    @Operation(summary = "Send application review notification email", description = "Send notification about new application for review")
    public boolean sendApplicationReviewNotificationToUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Generate mock application data
        String applicationId = "NEW-06-O" + System.currentTimeMillis() % 1000 + "-3-1-25-" + (userId + 5000000);
        String certType = "นิติบุคคล (Enterprise Certificate)";
        String companyName = user.getFullName() != null ? user.getFullName() : "บริษัท ตัวอย่าง จำกัด";
        String companyRegNo = "0105543" + (userId + 1000);

        return emailService.sendApplicationReviewNotification(
                user.getEmail(),
                user.getFullName(),
                applicationId,
                certType,
                companyName,
                companyRegNo
        );
    }

    @Operation(summary = "Send application confirmation email", description = "Send confirmation email for application signing")
    public boolean sendApplicationConfirmationToUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Generate mock application data
        String applicationId = "NEW-06-O" + System.currentTimeMillis() % 1000 + "-3-1-25-" + (userId + 5000000);
        String certType = "นิติบุคคล (Enterprise Certificate)";
        String companyName = user.getFullName() != null ? user.getFullName() : "บริษัท ตัวอย่าง จำกัด";
        String companyRegNo = "0105543" + (userId + 1000);
        String verifyLink = "https://thaidigitalid.com/ent-e-cert&No=PLacWddUgCVfmU09aWLT0991Sn-Ax65glr";

        return emailService.sendApplicationConfirmationEmail(
                user.getEmail(),
                user.getFullName(),
                applicationId,
                certType,
                companyName,
                companyRegNo,
                verifyLink
        );
    }

    @Operation(summary = "Send signature status notification email", description = "Send notification about signature signing status")
    public boolean sendSignatureStatusNotificationToUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Generate mock application data
        String applicationId = "NEW-06-O" + System.currentTimeMillis() % 1000 + "-3-1-25-" + (userId + 5000000);
        String certType = "นิติบุคคล (Enterprise Certificate)";
        String companyName = user.getFullName() != null ? user.getFullName() : "บริษัท ตัวอย่าง จำกัด";
        String companyRegNo = "0105543" + (userId + 1000);
        String signatureStatus = "0 จาก 3 ราย";
        java.util.List<String> signers = java.util.List.of(
            "นายสมชาย เก่งมาก (รอลงนาม)",
            "นางสาวสมหญิง สวยใส (รอลงนาม)",
            "นายสมศักดิ์ ชำนาญลาด (รอลงนาม)"
        );

        return emailService.sendSignatureStatusNotification(
                user.getEmail(),
                user.getEmail(),
                applicationId,
                certType,
                companyName,
                companyRegNo,
                signatureStatus,
                signers
        );
    }

    @Operation(summary = "Send signing completed notification email", description = "Send notification that signing is completed")
    public boolean sendSigningCompletedNotificationToUser(Long userId, String applicationId, String certType, String companyName, String companyRegNo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return emailService.sendSigningCompletedEmail(
                user.getEmail(),
                user.getEmail(),
                user.getFullName(),
                applicationId,
                certType,
                companyName,
                companyRegNo
        );
    }

    @Operation(summary = "Send certificate completion email", description = "Send certificate completion email with auth code and login link")
    public boolean sendCertificateCompletionToUser(Long userId, String applicationId, String certType, String companyName, String companyRegNo, String authCode, String validFrom, String validTo, String loginLink) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return emailService.sendCertificateCompletionEmail(
                user.getEmail(),
                user.getEmail(),
                user.getFullName(),
                applicationId,
                certType,
                companyName,
                companyRegNo,
                authCode,
                validFrom,
                validTo,
                loginLink
        );
    }

    @Operation(summary = "Send certificate rejection email", description = "Send certificate rejection email with rejection reason and fix link")
    public boolean sendCertificateRejectionToUser(Long userId, String applicationId, String certType, String companyName, String companyRegNo, String rejectionReason, String fixLink) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return emailService.sendCertificateRejectionEmail(
                user.getEmail(),
                user.getEmail(),
                user.getFullName(),
                applicationId,
                certType,
                companyName,
                companyRegNo,
                rejectionReason,
                fixLink
        );
    }

    @Operation(summary = "Send certificate download notification email", description = "Send certificate download email with serial number, status, and download link")
    public boolean sendCertificateDownloadToUser(Long userId, String applicationId, String serialNumber, String status, String statusColor, String validFrom, String validTo, String downloadLink) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return emailService.sendCertificateDownloadEmail(
                user.getEmail(),
                user.getEmail(),
                user.getFullName(),
                applicationId,
                serialNumber,
                status,
                statusColor,
                validFrom,
                validTo,
                downloadLink
        );
    }

    @Operation(summary = "Send new admin account notification email", description = "Send new admin account credentials and activation link")
    public boolean sendNewAdminToUser(Long userId, String username, String password, String activationLink) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return emailService.sendNewAdminEmail(
                user.getEmail(),
                username,
                password,
                activationLink
        );
    }

    @Operation(summary = "Send password reset notification email", description = "Send new password and login link to user")
    public boolean sendNewPasswordToUser(Long userId, String newPassword, String loginUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return emailService.sendNewPasswordEmail(
                user.getEmail(),
                newPassword,
                loginUrl
        );
    }

    @Operation(summary = "Send OTP verification email", description = "Send OTP code and reference code to user")
    public boolean sendOtpToUser(Long userId, String otp, String refCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return emailService.sendOtpEmail(
                user.getEmail(),
                otp,
                refCode
        );
    }

    @Operation(summary = "Send welcome email with Application ID", description = "Send welcome email with Application ID and validity period")
    public boolean sendWelcomeEmailToUser(Long userId, String applicationId, int validDays, String validFrom, String validTo, String loginUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return emailService.sendWelcomeEmail(
                user.getEmail(),
                applicationId,
                validDays,
                validFrom,
                validTo,
                loginUrl
        );
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a single user by their ID")
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDTO(user);
    }

    @Operation(summary = "Create user", description = "Create a new user account")
    public UserDTO createUser(CreateUserDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseGet(() -> roleRepository.save(new Role(null, request.getRoleName())));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    @Operation(summary = "Update user", description = "Update an existing user")
    public UserDTO updateUser(Long id, UpdateUserDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Prevent admin from editing themselves
        String currentUsername = getCurrentUsername();
        if (currentUsername != null && currentUsername.equals(user.getUsername())) {
            throw new RuntimeException("You cannot edit your own account");
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getRoleName() != null) {
            Role role = roleRepository.findByName(request.getRoleName())
                    .orElseGet(() -> roleRepository.save(new Role(null, request.getRoleName())));
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Operation(summary = "Delete user", description = "Delete a user by ID")
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Prevent admin from deleting themselves
        String currentUsername = getCurrentUsername();
        if (currentUsername != null && currentUsername.equals(user.getUsername())) {
            throw new RuntimeException("You cannot delete your own account");
        }

        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().getName()
        );
    }
}
