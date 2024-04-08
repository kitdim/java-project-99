package hexlet.code.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC+6")
    private Instant createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC+6")
    private Instant updatedAt;
}