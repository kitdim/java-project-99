package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.TemporalType.TIMESTAMP;


@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Task implements BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private Integer index;
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private TaskStatus taskStatus;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private User assignee;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<Label> labels = new HashSet<>();
    @Column(name = "created_at")
    @CreatedDate
    @Temporal(TIMESTAMP)
    private Date createdAt;
}
