package relead.relead_schoolmanagement.services;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import relead.relead_schoolmanagement.entities.Admin;
import relead.relead_schoolmanagement.exceptions.AppExceptions;
import relead.relead_schoolmanagement.repositories.AdminRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminService implements IAdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public Admin create(Admin admin) {
        if (admin.getUsername() == null || admin.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (admin.getPassword() == null || admin.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        Optional<Admin> existing = adminRepository.findByUsername(admin.getUsername());
        if (existing.isPresent()) {
            throw new AppExceptions.ResourceConflictException("Username already exists: " + admin.getUsername());
        }

        String rawPassword = admin.getPassword();
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
            throw new AppExceptions.BadRequestException("Invalid credentials");
        }
        return adminRepository.save(admin);
    }

    public Admin getByUsername(String username) {
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Admin not found with username: " + username));
    }

    public boolean authenticate(String username, String rawPassword) {
        Admin admin = getByUsername(username);
        return passwordEncoder.matches(rawPassword, admin.getPassword());
    }

    public void delete(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Admin not found with id: " + id));
        adminRepository.delete(admin);
    }
}
