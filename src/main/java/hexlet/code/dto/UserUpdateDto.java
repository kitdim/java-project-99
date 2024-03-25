package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public final class UserUpdateDto {
    private JsonNullable<String> firstName;
    private JsonNullable<String> lastName;
    @Email
    private JsonNullable<String> email;
    @NotBlank
    @Size(min = 3)
    private JsonNullable<String> password;
}
