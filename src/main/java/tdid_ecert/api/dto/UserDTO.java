package tdid_ecert.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information response")
public class UserDTO {
    @Schema(description = "User ID", example = "1")
    private Long id;
    @Schema(description = "Username", example = "john_doe")
    private String username;
    @Schema(description = "Email address", example = "john@example.com")
    private String email;
    @Schema(description = "Full name", example = "John Doe")
    private String fullName;
    @Schema(description = "Role name", example = "USER")
    private String roleName;
}
