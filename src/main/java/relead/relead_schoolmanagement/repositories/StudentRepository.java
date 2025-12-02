package relead.relead_schoolmanagement.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import relead.relead_schoolmanagement.entities.Level;
import relead.relead_schoolmanagement.entities.Student;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    Page<Student> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<Student> findByLevel(Level level, Pageable pageable);
}
