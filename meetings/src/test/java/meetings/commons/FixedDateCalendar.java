package meetings.commons;

import commons.calendar.Calendar;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class FixedDateCalendar implements Calendar {
    private final LocalDate localDate;

    @Override
    public LocalDate getCurrentDate() {
        return localDate;
    }
}