package relead.relead_schoolmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;


}