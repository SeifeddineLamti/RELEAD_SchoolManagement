package relead.relead_schoolmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import relead.relead_schoolmanagement.dto.AuthenticationRequest;
import relead.relead_schoolmanagement.dto.AuthenticationResponse;
import relead.relead_schoolmanagement.dto.RegisterRequest;
import relead.relead_schoolmanagement.services.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operations for Login and Registration")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @Operation(summary = "Register a new admin", description = "Create a new admin account and return a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - Admin registered and token returned"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Missing or invalid data", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict - Username already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate admin", description = "Authenticate using username/password and return a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - Authentication successful, token returned"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid format", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials (username or password)", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content)
    })
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}