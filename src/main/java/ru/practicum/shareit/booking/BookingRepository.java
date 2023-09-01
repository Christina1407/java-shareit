package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndStatusInOrderByStartDateDesc(Long userId, List<EnumStatus> status);
    List<Booking> findByBooker_IdAndStatusInAndStartDateGreaterThanEqualOrderByStartDateDesc(Long userId, List<EnumStatus> status, LocalDateTime localDateTime);
}
