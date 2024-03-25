package hexlet.code.component;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class DataInitializer implements ApplicationRunner {
    @Autowired
    private final UserRepository repository;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = new User();
        user.setPassword("qwerty");
        user.setEmail("hexlet@example.com");
        repository.save(user);
    }
}
