package relead.relead_schoolmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import relead.relead_schoolmanagement.entities.Level;
import relead.relead_schoolmanagement.entities.Student;
import relead.relead_schoolmanagement.services.IStudentService;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "Operations related to student management")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final IStudentService studentService;

    @PostMapping
    @Operation(summary = "Créer un étudiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource Created - Student created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data (missing fields)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict - Username already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error - Unexpected error", content = @Content)
    })
    public ResponseEntity<Student> create(@RequestBody Student student) {
        return new ResponseEntity<>(studentService.create(student), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les étudiants (avec pagination)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - List retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<Page<Student>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Sort sort = null;
        if (sortBy != null && !sortBy.isBlank()) {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
            sort = Sort.by(direction, sortBy);
        }
        return ResponseEntity.ok(studentService.getAll(page, size, sort));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un étudiant par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - Student found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found - Student with this ID does not exist", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un étudiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - Student updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid data provided", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found - Student not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict - New username already taken", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        return ResponseEntity.ok(studentService.update(id, student));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un étudiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - Student deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found - Student not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher par nom d'utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - Search results retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<Page<Student>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(studentService.searchByUsername(query, page, size));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filtrer par niveau scolaire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - Filtered results retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid level value", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<Page<Student>> filterByLevel(
            @RequestParam Level level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(studentService.filterByLevel(level, page, size));
    }
}