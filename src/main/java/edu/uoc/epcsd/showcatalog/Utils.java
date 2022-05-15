package edu.uoc.epcsd.showcatalog;

import java.time.*;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static LocalDate dateToLocalDate(Date d) {
        Instant instant = d.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        return zdt.toLocalDate();
    }

    public static LocalTime dateToLocalTime(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 1);
        Instant instant = cal.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        return zdt.toLocalTime();
    }
}
