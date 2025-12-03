package relead.relead_schoolmanagement.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import relead.relead_schoolmanagement.entities.Level;
import relead.relead_schoolmanagement.entities.Student;

import java.io.ByteArrayInputStream;

public interface IStudentService {
    Page<Student> getAll(int page, int size, Sort sort);
    Page<Student> getAll(int page, int size);
    Student getById(Long id);
    Student create(Student s);
    Student update(Long id, Student updated);
    void delete(Long id);
    Page<Student> searchByUsername(String q, int page, int size);
    Page<Student> filterByLevel(Level level, int page, int size);
    void saveFromCsv(MultipartFile file);
    ByteArrayInputStream loadCsv();
}
