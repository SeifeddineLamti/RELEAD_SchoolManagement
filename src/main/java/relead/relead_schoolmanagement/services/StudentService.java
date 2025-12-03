package relead.relead_schoolmanagement.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import relead.relead_schoolmanagement.entities.Level;
import relead.relead_schoolmanagement.entities.Student;
import relead.relead_schoolmanagement.repositories.StudentRepository;

@Service
@AllArgsConstructor
public class StudentService implements IStudentService {
    private final StudentRepository studentRepository;
    private static final String STUDENT_NOT_FOUND_MSG = "Student not found with id ";

    @Override
    public Page<Student> getAll(int page, int size, Sort sort) {
        Pageable p = PageRequest.of(page, size, sort == null ? Sort.unsorted() : sort);
        return studentRepository.findAll(p);
    }

    @Override
    public Page<Student> getAll(int page, int size) {
        return getAll(page, size, null);
    }

    @Override
    public Student getById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(STUDENT_NOT_FOUND_MSG + id));
    }

    @Override
    public Student create(Student s) {
        if(s.getUsername() == null || s.getUsername().isBlank())
            throw new IllegalArgumentException("username is required");

        studentRepository.findByUsername(s.getUsername()).ifPresent(u -> {
            throw new RuntimeException("username already exists " + s.getUsername());
        });

        return studentRepository.save(s);
    }

    @Override
    public Student update(Long id, Student updated) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(STUDENT_NOT_FOUND_MSG + id));

        if(updated.getUsername() != null && !updated.getUsername().equals(s.getUsername())) {
            studentRepository.findByUsername(updated.getUsername()).ifPresent(u -> {
                throw new RuntimeException("username already exists " + updated.getUsername());
            });
            s.setUsername(updated.getUsername());
        }

        if(updated.getLevel() != null)
            s.setLevel(updated.getLevel());

        return studentRepository.save(s);
    }

    @Override
    public void delete(Long id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(STUDENT_NOT_FOUND_MSG + id));

        studentRepository.delete(s);
    }

    @Override
    public Page<Student> searchByUsername(String q, int page, int size) {
        Pageable p = PageRequest.of(page, size);
        return studentRepository.findByUsernameContainingIgnoreCase(q == null ? "" : q, p);
    }

    @Override
    public Page<Student> filterByLevel(Level level, int page, int size) {
        Pageable p = PageRequest.of(page, size);
        return studentRepository.findByLevel(level, p);
    }

}
