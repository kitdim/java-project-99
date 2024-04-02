package hexlet.code.component;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.sevice.CustomUserDetailsService;
import hexlet.code.util.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class DataInitializer implements ApplicationRunner {
    private final CustomUserDetailsService userService;
    private final UserUtils userUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User admin = userUtils.getAdmin();
        userService.createUser(admin);
    }
}
