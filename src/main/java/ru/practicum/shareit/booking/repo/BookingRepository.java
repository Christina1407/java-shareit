package ru.practicum.shareit.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    List<Booking> findByBooker_IdAndStatusInOrderByStartDateDesc(Long userId, List<EnumStatus> status);

    List<Booking> findByBooker_IdOrderByStartDateDesc(Long userId);

    List<Booking> findByBooker_IdAndStartDateGreaterThanEqualOrderByStartDateDesc(Long userId, LocalDateTime localDateTime);

    List<Booking> findByBooker_IdAndEndDateLessThanEqualOrderByStartDateDesc(Long userId, LocalDateTime localDateTime);

    @Query("FROM Booking b WHERE b.booker.id = ?1 and ?2 BETWEEN b.startDate AND endDate")
    List<Booking> findCurrentBookings(Long userId, LocalDateTime localDateTime);

    List<Booking> findByItem_IdInAndStatusInOrderByStartDateDesc(List<Long> itemIds, List<EnumStatus> status);

    List<Booking> findByItem_IdInOrderByStartDateDesc(List<Long> itemIds);

    List<Booking> findByItem_IdInAndStartDateGreaterThanEqualOrderByStartDateDesc(List<Long> itemIds, LocalDateTime localDateTime);

    Booking findTopByItem_IdAndStartDateGreaterThanEqualAndStatusNotInOrderByStartDate(Long itemId, LocalDateTime localDateTime, List<EnumStatus> status);

    List<Booking> findByItem_IdInAndEndDateLessThanEqualOrderByStartDateDesc(List<Long> itemIds, LocalDateTime localDateTime);

    Booking findTopByItem_IdAndStartDateLessThanEqualAndStatusNotInOrderByStartDateDesc(Long itemId, LocalDateTime localDateTime, List<EnumStatus> status);

    @Query("FROM Booking b WHERE b.item.id in (?1) and ?2 BETWEEN b.startDate AND endDate")
    List<Booking> findCurrentOwnerBookings(List<Long> itemIds, LocalDateTime localDateTime);

    List<Booking> findByItem_IdAndStatusInAndBooker_IdAndEndDateLessThanEqual(Long itemId, List<EnumStatus> status, Long bookerId, LocalDateTime localDateTime);
}
