package relead.relead_schoolmanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import relead.relead_schoolmanagement.dto.AuthenticationRequest;
import relead.relead_schoolmanagement.dto.AuthenticationResponse;
import relead.relead_schoolmanagement.dto.RegisterRequest;
import relead.relead_schoolmanagement.entities.Admin;
import relead.relead_schoolmanagement.entities.Level;
import relead.relead_schoolmanagement.entities.Student;
import relead.relead_schoolmanagement.exceptions.AppExceptions;
import relead.relead_schoolmanagement.repositories.AdminRepository;
import relead.relead_schoolmanagement.repositories.StudentRepository;
import relead.relead_schoolmanagement.services.AdminService;
import relead.relead_schoolmanagement.services.AuthenticationService;
import relead.relead_schoolmanagement.services.JwtService;
import relead.relead_schoolmanagement.services.StudentService;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReleadSchoolManagementApplicationTests {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private StudentService studentService;

    @InjectMocks
    private AuthenticationService authService;

    @InjectMocks
    private AdminService adminService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student(1L, "ahmed", Level.FRESHMAN);
    }

    // --- ADMIN SERVICE TESTS ---

    @Test
    void shouldCreateAdmin_WhenValid() {
        Admin admin = new Admin(null, "admin", "password");
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        Admin created = adminService.create(admin);

        assertNotNull(created);
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void shouldThrowConflict_WhenAdminExists() {
        Admin admin = new Admin(null, "admin", "password");
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(new Admin()));

        assertThrows(AppExceptions.ResourceConflictException.class, () -> adminService.create(admin));
        verify(adminRepository, never()).save(any());
    }

    @Test
    void shouldAuthenticateAdmin_Fail_WrongPassword() {
        String username = "admin";
        Admin storedAdmin = new Admin(1L, username, "encodedPassword");

        when(adminRepository.findByUsername(username)).thenReturn(Optional.of(storedAdmin));
        when(passwordEncoder.matches("wrong", storedAdmin.getPassword())).thenReturn(false);

        boolean isAuthenticated = adminService.authenticate(username, "wrong");

        assertFalse(isAuthenticated);
    }

    @Test
    void shouldDeleteAdmin_WhenExists() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(new Admin()));
        adminService.delete(1L);
        verify(adminRepository).delete(any(Admin.class));
    }

    @Test
    void shouldThrowNotFound_WhenDeleteUnknownId() {
        when(adminRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(AppExceptions.ResourceNotFoundException.class, () -> adminService.delete(99L));
    }

    // --- AUTHENTICATION SERVICE TESTS ---

    @Test
    void shouldRegister_Success() {
        RegisterRequest request = new RegisterRequest("newAdmin", "pass123");
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");
        when(adminRepository.save(any(Admin.class))).thenReturn(new Admin());
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("mocked-jwt-token");

        AuthenticationResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void shouldAuthenticateUser_Success() {
        AuthenticationRequest request = new AuthenticationRequest("admin", "pass123");
        Admin admin = new Admin(1L, "admin", "encodedPass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("mocked-jwt-token");

        AuthenticationResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
    }

    @Test
    void shouldThrowException_WhenAuthenticateUserNotFound() {
        AuthenticationRequest request = new AuthenticationRequest("unknown", "pass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(adminRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.authenticate(request));
        assertEquals("Admin not found", ex.getMessage());
    }

    // --- STUDENT SERVICE TESTS ---

    @Test
    void shouldCreateStudent_Success() {
        when(studentRepository.findByUsername("ahmed")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student result = studentService.create(student);

        assertNotNull(result);
        assertEquals("ahmed", result.getUsername());
    }

    @Test
    void shouldThrowException_WhenCreatingDuplicateUsername() {
        when(studentRepository.findByUsername("ahmed")).thenReturn(Optional.of(student));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> studentService.create(student));
        assertTrue(ex.getMessage().contains("username already exists"));
    }

    @Test
    void shouldThrowException_WhenCreateStudentWithInvalidUsername() {
        Student nullUser = new Student(null, null, Level.FRESHMAN);
        assertThrows(IllegalArgumentException.class, () -> studentService.create(nullUser));

        Student blankUser = new Student(null, "", Level.FRESHMAN);
        assertThrows(IllegalArgumentException.class, () -> studentService.create(blankUser));

        verify(studentRepository, never()).save(any());
    }

    @Test
    void shouldGetAllStudents_Paginated() {
        Page<Student> page = new PageImpl<>(Collections.singletonList(student));
        when(studentRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Student> result = studentService.getAll(0, 10);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetStudentById_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Student result = studentService.getById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowException_WhenGetStudentByIdNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> studentService.getById(99L));
        assertTrue(ex.getMessage().contains("Student not found"));
    }

    @Test
    void shouldUpdateStudent_Success() {
        Student updateInfo = new Student(null, "ahmed_new", Level.SENIOR);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.findByUsername("ahmed_new")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student updated = studentService.update(1L, updateInfo);

        assertEquals("ahmed_new", updated.getUsername());
        assertEquals(Level.SENIOR, updated.getLevel());
    }

    @Test
    void shouldThrowException_WhenUpdateStudentNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        Student updateRequest = new Student();
        assertThrows(RuntimeException.class, () -> studentService.update(99L, updateRequest));
    }

    @Test
    void shouldThrowException_WhenUpdateStudentUsernameDuplicate() {
        Student existingStudent = new Student(1L, "oldName", Level.FRESHMAN);
        Student updateRequest = new Student(null, "takenName", Level.SENIOR);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.findByUsername("takenName")).thenReturn(Optional.of(new Student(2L, "takenName", Level.SENIOR)));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> studentService.update(1L, updateRequest));
        assertTrue(ex.getMessage().contains("username already exists"));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void shouldDeleteStudent_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        studentService.delete(1L);
        verify(studentRepository).delete(student);
    }

    @Test
    void shouldThrowException_WhenDeleteStudentNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> studentService.delete(99L));
        verify(studentRepository, never()).delete(any());
    }

    @Test
    void shouldSearchByUsername() {
        Page<Student> page = new PageImpl<>(Collections.singletonList(student));
        when(studentRepository.findByUsernameContainingIgnoreCase(eq("ahmed"), any(Pageable.class))).thenReturn(page);

        Page<Student> result = studentService.searchByUsername("ahmed", 0, 10);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFilterByLevel() {
        Page<Student> page = new PageImpl<>(Collections.singletonList(student));
        when(studentRepository.findByLevel(eq(Level.FRESHMAN), any(Pageable.class))).thenReturn(page);

        Page<Student> result = studentService.filterByLevel(Level.FRESHMAN, 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(Level.FRESHMAN, result.getContent().get(0).getLevel());
    }
}