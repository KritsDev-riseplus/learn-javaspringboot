package tdid_ecert.api.config;

import tdid_ecert.api.entities.Role;
import tdid_ecert.api.entities.User;
import tdid_ecert.api.repositories.RoleRepository;
import tdid_ecert.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create roles
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "USER")));
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));

        // Create admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFullName("Administrator");
            admin.setRole(adminRole);
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin / admin123");
        }

        // Create test users
        if (!userRepository.existsByUsername("user1")) {
            User user1 = new User();
            user1.setUsername("user1");
            user1.setPassword(passwordEncoder.encode("password123"));
            user1.setEmail("user1@example.com");
            user1.setFullName("Test User One");
            user1.setRole(userRole);
            userRepository.save(user1);
            System.out.println("✅ User1 created: user1 / password123");
        }

        System.out.println("🎉 Data initialization completed!");
    }
}
