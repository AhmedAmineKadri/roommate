package example.roommate.Domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import jakarta.validation.constraints.*;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
@Table("reservation")
public class ReservationFormular {
    @Id
    private Long reservationId;
    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date must not be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate tag;
    @NotNull(message = "Start time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;
    @NotNull(message = "End time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;
    @NotNull(message = "Choose a workspace")
    private Long arbeitsplatzId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationFormular that = (ReservationFormular) o;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }

    @PersistenceCreator
    public ReservationFormular(Long reservationId, LocalDate tag, LocalTime startTime, LocalTime endTime, Long arbeitsplatzId) {
        this.reservationId = reservationId;
        this.tag = tag;
        this.startTime = startTime;
        this.endTime = endTime;
        this.arbeitsplatzId = arbeitsplatzId;
    }

    public ReservationFormular(){
    }

    public ReservationFormular(LocalDate tag, LocalTime startTime, LocalTime endTime, Long arbeitsplatzId) {
        this(null, tag,startTime,endTime,arbeitsplatzId);
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDate getTag() {
        return tag;
    }

    public void setTag(LocalDate tag) {
        this.tag = tag;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Long getArbeitsplatzId() {
        return arbeitsplatzId;
    }

    public void setArbeitsplatzId(Long arbeitsplatzId) {
        this.arbeitsplatzId = arbeitsplatzId;
    }
}
