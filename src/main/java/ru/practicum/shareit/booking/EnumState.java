package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;
}
