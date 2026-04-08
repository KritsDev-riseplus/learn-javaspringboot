package tdid_ecert.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response containing token and user info")
public class AuthResponse {
    @Schema(description = "Authentication token", example = "token-admin")
    private String token;
    @Schema(description = "Username", example = "admin")
    private String username;
    @Schema(description = "User role", example = "ADMIN")
    private String role;
}
