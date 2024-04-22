package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    @NotNull
    private String title;

    private String content;

    private Integer index;

    @NotNull
    private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;
}
