package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	private static final String USER_ID = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> saveBooking(@RequestHeader(USER_ID) @Min(1) Long userId,
										   @RequestBody @Validated(OnCreate.class) @Valid BookingDtoRequest requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.saveBooking(userId, requestDto);
	}

	@PatchMapping("{bookingId}")
	public ResponseEntity<Object> approveOrRejectBooking(@PathVariable("bookingId") @Min(1) Long bookingId,
													 @RequestHeader(USER_ID) @NotNull @Min(1) Long userId,
													 @RequestParam @NotNull Boolean approved) {
		log.info("Попытка обновления booking id = {}", bookingId);
		return bookingClient.approveOrRejectBooking(bookingId, userId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findBookingById(@RequestHeader(USER_ID) @NotNull @Min(1) Long userId,
												  @PathVariable @Min(1) Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.findBookingById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> findUsersBookings(@RequestHeader(USER_ID) @NotNull @Min(1) Long bookerId,
											  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
											  @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
											  @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, bookerId, from, size);
		return bookingClient.findUsersBookings(bookerId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findOwnersBookings(@RequestHeader(USER_ID) @NotNull @Min(1) Long ownerId,
													   @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
													   @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
													   @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.findOwnersBookings(ownerId, state, from, size);
	}
}


