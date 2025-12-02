package relead.relead_schoolmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import relead.relead_schoolmanagement.entities.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);

}
