package tdid_ecert.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create user request")
public class CreateUserDTO {
    @Schema(description = "Username", example = "newuser", required = true)
    private String username;
    @Schema(description = "Password", example = "password123", required = true)
    private String password;
    @Schema(description = "Email address", example = "newuser@example.com", required = true)
    private String email;
    @Schema(description = "Full name", example = "New User")
    private String fullName;
    @Schema(description = "Role name", example = "USER", required = true)
    private String roleName;
}
