package relead.relead_schoolmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import relead.relead_schoolmanagement.entities.Admin;
import relead.relead_schoolmanagement.services.AdminService;

@RestController
@RequestMapping("/api/admins")
@AllArgsConstructor
@Tag(name = "Admin Management", description = "Operations for managing Admin profiles")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    @Operation(summary = "Create a new admin manually", description = "Add a new admin directly to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - Admin created"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing/invalid", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict - Username already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<Admin> create(@RequestBody Admin admin) {
        Admin saved = adminService.create(admin);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get admin by username", description = "Retrieve admin details by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - Admin found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing/invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found - Admin not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<Admin> getByUsername(@PathVariable String username) {
        Admin admin = adminService.getByUsername(username);
        return ResponseEntity.ok(admin);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an admin", description = "Remove an admin account by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - Admin deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing/invalid", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found - Admin not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<?> delete(@PathVariable Long id) {
        adminService.delete(id);
        return ResponseEntity.ok().build();
    }
}