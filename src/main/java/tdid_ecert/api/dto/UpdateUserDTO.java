package tdid_ecert.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update user request")
public class UpdateUserDTO {
    @Schema(description = "Email address", example = "updated@example.com")
    private String email;
    @Schema(description = "Full name", example = "Updated User")
    private String fullName;
    @Schema(description = "Role name", example = "ADMIN")
    private String roleName;
}
