package hexlet.code.repository;

import hexlet.code.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Set<Label> findByIdIn(Set<Long> labelIds);
    Optional<Label> findByName(String name);
}
