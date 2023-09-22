package ru.practicum.shareit.validation;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.dto.BookingDtoRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckDateValidatorTest {
    private static final Validator validator;

    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    void shouldBeValidated() {
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();
        Set<ConstraintViolation<BookingDtoRequest>> violations = validator.validate(bookingDtoRequest);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(elem -> elem.getMessage().equals("Start must be before end or not null")));
    }
}