package ru.practicum.shareit.booking.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.enums.EnumStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    List<Booking> findByBooker_IdAndStatusInOrderByStartDateDesc(Long userId, List<EnumStatus> status, Pageable pageable);

    List<Booking> findByBooker_IdOrderByStartDateDesc(Long userId, Pageable pageable);

    List<Booking> findByBooker_IdAndStartDateGreaterThanEqualOrderByStartDateDesc(Long userId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByBooker_IdAndEndDateLessThanEqualOrderByStartDateDesc(Long userId, LocalDateTime localDateTime, Pageable pageable);

    @Query("FROM Booking b WHERE b.booker.id = ?1 and ?2 BETWEEN b.startDate AND endDate")
    List<Booking> findCurrentBookings(Long userId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByItem_IdInAndStatusInOrderByStartDateDesc(List<Long> itemIds, List<EnumStatus> status, Pageable pageable);

    List<Booking> findByItem_IdInOrderByStartDateDesc(List<Long> itemIds, Pageable pageable);

    List<Booking> findByItem_IdInAndStartDateGreaterThanEqualOrderByStartDateDesc(List<Long> itemIds, LocalDateTime localDateTime, Pageable pageable);

    Booking findTopByItem_IdAndStartDateGreaterThanEqualAndStatusNotInOrderByStartDate(Long itemId, LocalDateTime localDateTime, List<EnumStatus> status);

    List<Booking> findByItem_IdInAndEndDateLessThanEqualOrderByStartDateDesc(List<Long> itemIds, LocalDateTime localDateTime, Pageable pageable);

    Booking findTopByItem_IdAndStartDateLessThanEqualAndStatusNotInOrderByStartDateDesc(Long itemId, LocalDateTime localDateTime, List<EnumStatus> status);

    @Query("FROM Booking b WHERE b.item.id in (?1) and ?2 BETWEEN b.startDate AND endDate")
    List<Booking> findCurrentOwnerBookings(List<Long> itemIds, LocalDateTime localDateTime, Pageable pageable);

    boolean existsByItem_IdAndStatusInAndBooker_IdAndEndDateLessThanEqual(Long itemId, List<EnumStatus> status, Long bookerId, LocalDateTime localDateTime);
}
