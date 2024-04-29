package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CustomUserDetailsService userService;
    private final LabelRepository labelRepository;
    private final TaskStatusRepository taskStatusRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            var userData = new User();
            userData.setEmail(email);
            userData.setPasswordDigest("qwerty");
            userService.createUser(userData);
        }
        List<TaskStatus> statuses = getTaskStatuses();

        for (var status : statuses) {
            taskStatusRepository.save(status);
        }

        var labelBug = new Label();
        labelBug.setName("bug");
        labelRepository.save(labelBug);
        var labelFeature = new Label();
        labelFeature.setName("feature");
        labelRepository.save(labelFeature);
    }

    private static List<TaskStatus> getTaskStatuses() {
        List<TaskStatus> statuses = new ArrayList<>();
        TaskStatus draft = new TaskStatus();
        draft.setName("draft");
        draft.setSlug("draft");
        statuses.add(draft);

        TaskStatus toReview = new TaskStatus();
        toReview.setName("toReview");
        toReview.setSlug("to_review");
        statuses.add(toReview);

        TaskStatus toBeFixed = new TaskStatus();
        toBeFixed.setName("toBeFixed");
        toBeFixed.setSlug("to_be_fixed");
        statuses.add(toBeFixed);

        TaskStatus toPublish = new TaskStatus();
        toPublish.setName("toPublish");
        toPublish.setSlug("to_publish");
        statuses.add(toPublish);

        TaskStatus published = new TaskStatus();
        published.setName("published");
        published.setSlug("published");
        statuses.add(published);
        return statuses;
    }
}
