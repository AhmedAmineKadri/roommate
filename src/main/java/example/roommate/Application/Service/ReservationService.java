package example.roommate.Application.Service;

import example.roommate.Domain.model.ReservationFormular;
import example.roommate.DataBase.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    private final ReservationRepository reservationRepository;

    public void saveReservation(ReservationFormular reservation){

        LocalTime start = reservation.getStartTime();
        LocalTime end = reservation.getEndTime();
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end times are required");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        reservationRepository.save(reservation);
    }

    public void validateTime(ReservationFormular reservation, BindingResult bindingResult) {
        if (reservation.getTag().isEqual(LocalDate.now()) && reservation.getStartTime().isBefore(LocalTime.now())) {
            bindingResult.addError(new FieldError("reservation", "startTime", "StartTime can't be in the past"));
        }
        if (!reservation.getEndTime().isAfter(reservation.getStartTime())) {
            bindingResult.addError(new FieldError("reservation", "endTime", "End time must be after start time"));
        }
    }


    public Boolean checkReservationExists(ReservationFormular reservation) {
        return reservationRepository.reservationExists(reservation.getArbeitsplatzId(),
                                                reservation.getTag(),
                                                reservation.getStartTime(),
                                                reservation.getEndTime());
    }

    public List<ReservationFormular> getAllReservation(){
        return (List<ReservationFormular>) reservationRepository.findAll();
    }

    public void deleteReservationById(Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }
}
