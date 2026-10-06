package example.roommate.ServicesTests;

import example.roommate.Application.Service.ReservationService;
import example.roommate.DataBase.ReservationRepository;
import example.roommate.Domain.model.ReservationFormular;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Test
    void savesReservationWhenEndIsAfterStart() {
        // Arrange: prepare the objects
        ReservationRepository repository = mock(ReservationRepository.class);
        ReservationService service = new ReservationService(repository);

        ReservationFormular reservation = new ReservationFormular(
                LocalDate.of(2026, 10, 9),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                5L
        );

        // Act: call the method
        service.saveReservation(reservation);

        // Assert: check the interaction
        verify(repository).save(reservation);
    }

    @Test
    void rejectsReservationWhenTimesAreEqual() {
        ReservationRepository repository = mock(ReservationRepository.class);
        ReservationService service = new ReservationService(repository);

        ReservationFormular reservation = new ReservationFormular(
                LocalDate.of(2026, 10, 9),
                LocalTime.of(10, 0),
                LocalTime.of(10, 0),
                5L
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.saveReservation(reservation)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsReservationWhenEndIsBeforeStart() {
        ReservationRepository repository = mock(ReservationRepository.class);
        ReservationService service = new ReservationService(repository);

        ReservationFormular reservation = new ReservationFormular(
                LocalDate.of(2026, 10, 9),
                LocalTime.of(10, 0),
                LocalTime.of(9, 0),
                5L
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.saveReservation(reservation)
        );

        verify(repository, never()).save(any());
    }
    @Test
    void rejectsReservationWhenStartIsMissing() {
        ReservationRepository repository = mock(ReservationRepository.class);
        ReservationService service = new ReservationService(repository);

        ReservationFormular reservation = new ReservationFormular(
                LocalDate.of(2026, 10, 9),
                null,
                LocalTime.of(11, 0),
                5L
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.saveReservation(reservation)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsReservationWhenEndIsMissing() {
        ReservationRepository repository = mock(ReservationRepository.class);
        ReservationService service = new ReservationService(repository);

        ReservationFormular reservation = new ReservationFormular(
                LocalDate.of(2026, 10, 9),
                LocalTime.of(10, 0),
                null,
                5L
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.saveReservation(reservation)
        );

        verify(repository, never()).save(any());
    }
}
