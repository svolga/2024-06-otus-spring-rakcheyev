package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final CsvQuestionDao csvQuestionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = csvQuestionDao.findAll();
        printQuestions(questions);
    }

    private void printQuestions(List<Question> questions) {
        AtomicInteger i = new AtomicInteger();
        questions.forEach(question -> {
            ioService.printFormattedLine("%d. %s", i.incrementAndGet(), question.text());
            ioService.printLine("Answers:");
            question.answers().forEach(answer -> ioService.printLine(" - " + answer.text()));
            ioService.printLine("");
        });
    }
}
