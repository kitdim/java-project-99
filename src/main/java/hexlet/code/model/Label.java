package hexlet.code.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Temporal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@EntityListeners(AuditingEntityListener.class)
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Label {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    private Long id;

    @Column(unique = true)
    @NotBlank
    @Size(min = 3, max = 1000)
    @ToString.Include
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "labels", cascade = CascadeType.MERGE)
    private Set<Task> tasks = new HashSet<>();

    @Column(name = "created_at")
    @CreatedDate
    @Temporal(TIMESTAMP)
    private Date createdAt;
}
