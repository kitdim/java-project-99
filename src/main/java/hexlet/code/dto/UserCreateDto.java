package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public final class UserCreateDto {
    private String firstName;
    private String lastName;
    @Email
    private String email;
    @NotBlank
    @Size(min = 3)
    private String password;
}
