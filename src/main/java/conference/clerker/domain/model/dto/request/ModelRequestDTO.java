package conference.clerker.domain.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ModelRequestDTO(
        @NotEmpty
        List<String> keywords,
        @NotBlank
        String mp3FileUrl
) {}
