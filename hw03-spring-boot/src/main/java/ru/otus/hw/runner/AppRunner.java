package ru.otus.hw.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.TestRunnerService;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class AppRunner implements CommandLineRunner {

    private final TestRunnerService testRunnerService;

    @Override
    public void run(String... args) throws Exception {
        testRunnerService.run();
    }
}
