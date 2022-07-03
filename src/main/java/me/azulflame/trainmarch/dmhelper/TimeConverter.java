package me.azulflame.trainmarch.dmhelper;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeConverter {
    public static String getTime(String time) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(time, dtf);
            return "Your string is `<t:" + zonedDateTime.toEpochSecond() + ">`, which shows as <t:"
                    + zonedDateTime.toEpochSecond() + ">\nOr you can do `<t:" + zonedDateTime.toEpochSecond()
                    + ":R>` for <t:" + zonedDateTime.toEpochSecond() + ":R>";
        } catch (DateTimeParseException e) {
            return "Unable to parse date, please use the format `YYYY-MM-DD HH:MM:SS Z`";
        }
    }
}
