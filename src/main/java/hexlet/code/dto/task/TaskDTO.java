package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private long id;
    private String title;
    private String content;
    private Integer index;
    private String status;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private Set<Long> taskLabelIds;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC+6")
    private Date createdAt;
}
