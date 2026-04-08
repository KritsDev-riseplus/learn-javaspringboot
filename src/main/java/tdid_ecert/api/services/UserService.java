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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Operation(summary = "Send email to user", description = "Send a custom email message to a specific user")
    public boolean sendEmailToUser(Long userId, String subject, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return emailService.sendCustomEmail(
                user.getEmail(),
                subject,
                user.getFullName(),
                content,
                getCurrentUsername()
        );
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

        // Send notification email
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", savedUser.getUsername());
        userInfo.put("email", savedUser.getEmail());
        userInfo.put("fullName", savedUser.getFullName());
        userInfo.put("role", savedUser.getRole().getName());

        emailService.sendAccountNotification(
                savedUser.getEmail(),
                "Your TDID E-Cert Account Has Been Created",
                userInfo
        );

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
