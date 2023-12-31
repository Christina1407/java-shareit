package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class, BookingController.class, ItemRequestController.class})
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ExceptionValidationResponse(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFoundException(final NotFoundException e) {
        return new ExceptionResponse("Искомый объект не найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleNotAllowedException(final NotAllowedException e) {
        return new ExceptionResponse("Объект недоступен");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleAlreadyExistsException(final AlreadyExistsException e) {
        return new ExceptionResponse("Искомый объект уже существует");
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBadRequestException(final Exception e) {
        if (e.getMessage().contains("EnumState")) {
            return new ExceptionResponse("Unknown state: UNSUPPORTED_STATUS");
        }
        return new ExceptionResponse("Некорректные параметры запроса");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleValidationException(final ValidationException e) {
        return new ExceptionResponse(e.getMessage());
    }
}
