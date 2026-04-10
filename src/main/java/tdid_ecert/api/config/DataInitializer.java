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
            admin.setEmail("kritsada.m@riseplus.tech");
            admin.setFullName("Administrator");
            admin.setRole(adminRole);
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin / admin123");
        }

        // Seed 10 test users
        String[][] users = {
            {"user1", "password123", "thanyarat.t@riseplus.tech", "Thanyarat Johnson"},
            {"user2", "password123", "user2@example.com", "Bob Smith"},
            {"user3", "password123", "user3@example.com", "Charlie Brown"},
            {"user4", "password123", "user4@example.com", "Diana Prince"},
            {"user5", "password123", "user5@example.com", "Edward Norton"},
            {"user6", "password123", "user6@example.com", "Fiona Apple"},
            {"user7", "password123", "user7@example.com", "George Lucas"},
            {"user8", "password123", "user8@example.com", "Hannah Montana"},
            {"user9", "password123", "user9@example.com", "Ivan Drago"},
            {"user10", "password123", "user10@example.com", "Julia Roberts"},
        };

        for (String[] userData : users) {
            String username = userData[0];
            String password = userData[1];
            String email = userData[2];
            String fullName = userData[3];

            if (!userRepository.existsByUsername(username)) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setEmail(email);
                user.setFullName(fullName);
                user.setRole(userRole);
                userRepository.save(user);
                System.out.println("✅ User created: " + username + " / " + password + " (" + fullName + ")");
            }
        }

        System.out.println("🎉 Data initialization completed! Total: 1 admin + 10 users");
    }
}
