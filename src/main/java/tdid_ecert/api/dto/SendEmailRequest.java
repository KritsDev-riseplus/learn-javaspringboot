package tdid_ecert.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Send email request")
public class SendEmailRequest {
    @Schema(description = "Email subject", example = "Important Update")
    private String subject;
    @Schema(description = "Email content", example = "Hello, this is an important update...")
    private String content;
}
