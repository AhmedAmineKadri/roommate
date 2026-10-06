package example.roommate.Web;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class WantedPeriod {
    @DateTimeFormat(pattern = "yyyy-MM-dd")  LocalDate date;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    LocalTime timeFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime timeTo;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(LocalTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public LocalTime getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(LocalTime timeTo) {
        this.timeTo = timeTo;
    }
}
