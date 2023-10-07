package ru.practicum.shareit.unit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.*;

import javax.validation.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsHandlerTest {
    private ExceptionsHandler exceptionsHandler;

    @BeforeEach
    void setUp() {
        exceptionsHandler = new ExceptionsHandler();
    }

    @Test
    void handleNotFoundException() {
        //before
        NotFoundException notFoundException = new NotFoundException();
        //when
        ExceptionResponse exceptionResponse = exceptionsHandler.handleNotFoundException(notFoundException);
        //then
        assertThat(exceptionResponse.getError()).isEqualTo("Искомый объект не найден");
    }

    @Test
    void handleNotAllowedException() {
        //before
        NotAllowedException notAllowedException = new NotAllowedException();
        //when
        ExceptionResponse exceptionResponse = exceptionsHandler.handleNotAllowedException(notAllowedException);
        //then
        assertThat(exceptionResponse.getError()).isEqualTo("Объект недоступен");
    }

    @Test
    void handleAlreadyExistsException() {
        //before
        AlreadyExistsException notAlreadyExistsException = new AlreadyExistsException();
        //when
        ExceptionResponse exceptionResponse = exceptionsHandler.handleAlreadyExistsException(notAlreadyExistsException);
        //then
        assertThat(exceptionResponse.getError()).isEqualTo("Искомый объект уже существует");
    }

    @Test
    void handleValidationException() {
        //before
        ValidationException validationException = new ValidationException("test");
        //when
        ExceptionResponse exceptionResponse = exceptionsHandler.handleValidationException(validationException);
        //then
        assertThat(exceptionResponse.getError()).isEqualTo("test");
    }

    @Test
    void handleBadRequestException() {
        //before
        Exception exception = new Exception("EnumState");
        //when
        ExceptionResponse exceptionResponse = exceptionsHandler.handleBadRequestException(exception);
        //then
        assertThat(exceptionResponse.getError()).isEqualTo("Unknown state: UNSUPPORTED_STATUS");

        //before
        exception = new Exception("test");
        //when
        ExceptionResponse exceptionResponseNotEnumState = exceptionsHandler.handleBadRequestException(exception);
        //then
        assertThat(exceptionResponseNotEnumState.getError()).isEqualTo("Некорректные параметры запроса");
    }
}