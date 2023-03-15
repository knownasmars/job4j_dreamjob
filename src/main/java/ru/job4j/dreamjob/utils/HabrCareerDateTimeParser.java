package ru.job4j.dreamjob.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime parse(LocalDateTime localDateTime) {
        DateTimeFormatter dt = FORMATTER;
        String formatted = localDateTime.format(dt);
        LocalDateTime parsed = LocalDateTime.parse(formatted, FORMATTER);
        return parsed;
    }
}