package tdid_ecert.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication request containing username and password")
public class AuthRequest {
    @Schema(description = "Username", example = "admin")
    private String username;
    @Schema(description = "Password", example = "admin123")
    private String password;
}
