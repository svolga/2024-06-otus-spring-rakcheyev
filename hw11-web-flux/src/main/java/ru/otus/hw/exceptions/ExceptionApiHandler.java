package ru.otus.hw.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.hw.dto.ApiErrorDto;
import ru.otus.hw.utils.formatter.HttpStatusFormatter;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorDto handleMethodEntityNotFoundException(EntityNotFoundException e) {

        ApiErrorDto apiError = ApiErrorDto.builder()
                .status(HttpStatusFormatter.formatHttpStatus(HttpStatus.NOT_FOUND))
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("handleMethodEntityNotFoundException(EntityNotFoundException e) --> {}", e);
        return apiError;

    }



}
