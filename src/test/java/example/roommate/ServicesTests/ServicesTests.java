package example.roommate.ServicesTests;

import example.roommate.Application.Service.ReservationService;
import example.roommate.Application.Service.RoomRepository;
import example.roommate.DataBase.ReservationRepository;
import example.roommate.DataBase.RoomRepositoryImpl;
import example.roommate.DataBase.RoomDAO;
import example.roommate.Domain.model.Arbeitsplatz.Arbeitsplatz;
import example.roommate.Domain.model.ReservationFormular;
import example.roommate.Domain.model.Room.Room;
import example.roommate.DataBase.ArbeitsplatzRepository;
import example.roommate.Application.Service.ArbeitsplatzService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJdbcTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:roommate_test;CASE_INSENSITIVE_IDENTIFIERS=TRUE"
})
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class ServicesTests {
    public static final LocalDate DATE = LocalDate.of(2024, 02, 21);
    public static final LocalTime START_TIME = LocalTime.of(14, 30);
    public static final LocalTime END_TIME = LocalTime.of(16, 30);
    @Autowired
    RoomDAO roomDAO;

    @Autowired
    ArbeitsplatzRepository arbeitsplatzRepository;
    @Autowired
    ReservationRepository reservationRepository;

    RoomRepository roomRepository;
    ArbeitsplatzService arbeitsplatzService;

    ReservationService reservationService;

    @BeforeEach
    void setUp() {
        roomRepository = new RoomRepositoryImpl(roomDAO);
        arbeitsplatzService = new ArbeitsplatzService(arbeitsplatzRepository);
        reservationService = new ReservationService(reservationRepository);
    }


    private Room createRoom(String roomName) {
        Room room = new Room(roomName);
        roomRepository.save(room);
        return room;
    }

    private Long createAndAddRoom(String roomName) {
        Room room = new Room(roomName);
        return roomRepository.save(room).getRoomId();
    }
    private Long generateArbeitsplatzId() {
        Long room1Id = createAndAddRoom("neben Café");
        Arbeitsplatz arbeitsplatz = new Arbeitsplatz("TestArbeitsplatz", room1Id);
        Long arbeitsplatzId = arbeitsplatzService.addPlatzAndReturnID(arbeitsplatz);
        return arbeitsplatzId;
    }

    @Test
    @DisplayName("Der roomService kann Raum hinzufügen")
    void testAddSingleRoom() {
        // Arrange
        Room room = createRoom("neben Café");

        // Act
        List<Room> allRooms = roomRepository.findAll();


        // Assert
        assertThat(allRooms.getFirst().getName()).isEqualTo("neben Café");

    }

    @Test
    @DisplayName("Der roomService kann mehrere Räume hinzufügen")
    void testAddMultipleRooms() {
        // Arrange
        Room room1 = createRoom("neben Café");
        Room room2 = createRoom("neben Mensa");

        // Act
        List<Room> allRooms = roomRepository.findAll();

        // Assert
        assertThat(allRooms).size().isEqualTo(2);
    }

    @Test
    @DisplayName("Der ArbeitsplatzService kann Arbeitsplatz hinzufügen")
    void addSingleArbeitsplatzTest() {
        // Arrange
        Long room1Id = createAndAddRoom("neben Café");
        Arbeitsplatz arbeitsplatz = new Arbeitsplatz("TestArbeitsplatz", room1Id);

        // Act
        arbeitsplatzService.addPlatz(arbeitsplatz);
        List<Arbeitsplatz> allArbeitsplaetze = arbeitsplatzService.getAllArbeitsplaetze();

        // Assert
        assertThat(allArbeitsplaetze).contains(arbeitsplatz);
    }

    @Test
    @DisplayName("Der ArbeitsplatzService kann mehrere Arbeitsplätze hinzufügen")
    void addMultipleArbeitsplaetzeTest() {
        // Arrange
        Long room1Id = createAndAddRoom("neben Café");
        Long room2Id = createAndAddRoom("neben Mensa");
        Arbeitsplatz arbeitsplatz1 = new Arbeitsplatz("TestArbeitsplatz1", room1Id);
        Arbeitsplatz arbeitsplatz2 = new Arbeitsplatz("TestArbeitsplatz2", room2Id);

        // Act
        arbeitsplatzService.addPlatz(arbeitsplatz1);
        arbeitsplatzService.addPlatz(arbeitsplatz2);
        List<Arbeitsplatz> allArbeitsplaetze = arbeitsplatzService.getAllArbeitsplaetze();

        // Assert
        assertThat(allArbeitsplaetze).contains(arbeitsplatz1, arbeitsplatz2);
    }

    @Test
    @DisplayName("Test getAllRoomsNames")
    void testGetAllRoomsNames() {
        // Arrange
        createRoom("neben Café");
        createRoom("neben Mensa");

        // Act
        List<String> allRoomNames = roomRepository.getRoomsNames();

        // Assert
        assertThat(allRoomNames).contains("neben Café", "neben Mensa");
    }

    @Test
    @DisplayName("Test getAllArbeitsplaetzeNames")
    void testGetAllArbeitsplaetzeNames() {
        // Arrange
        Long room1Id = createAndAddRoom("neben Café");
        Long room2Id = createAndAddRoom("neben Mensa");
        Arbeitsplatz arbeitsplatz1 = new Arbeitsplatz("TestArbeitsplatz1", room1Id);
        Arbeitsplatz arbeitsplatz2 = new Arbeitsplatz("TestArbeitsplatz2", room2Id);
        arbeitsplatzService.addPlatz(arbeitsplatz1);
        arbeitsplatzService.addPlatz(arbeitsplatz2);

        // Act
        List<String> allArbeitsplaetzeNames = arbeitsplatzService.getAllArbeitsplaetzeNames();

        // Assert
        assertThat(allArbeitsplaetzeNames).contains("TestArbeitsplatz1", "TestArbeitsplatz2");
    }

    @Test
    @DisplayName("Test getRoomById")
    void testGetRoomById() {
        // Arrange
        Long roomId = createAndAddRoom("neben Café");

        // Act
        Room room = roomRepository.findById(roomId).orElse(null);

        // Assert
        assertThat(room.getName()).isEqualTo("neben Café");
    }

    @Test
    @DisplayName("Test getArbeitsplatzById")
    void testGetArbeitsplatzById() {
        // Arrange
        Long arbeitsplatzId = generateArbeitsplatzId();

        // Act
        Arbeitsplatz retrievedArbeitsplatz = arbeitsplatzService.getArbeitsplatzById(arbeitsplatzId);

        // Assert
        assertThat(retrievedArbeitsplatz.getName()).isEqualTo("TestArbeitsplatz");
    }

    @Test
    @DisplayName("Test updateRoom")
    void testUpdateRoom() {
        // Arrange
        Long roomId = createAndAddRoom("neben Café");
        Room room = roomRepository.findById(roomId).orElse(null);
        room.setName("neben Mensa");

        // Act
        roomRepository.save(room);
        Room updatedRoom = roomRepository.findById(roomId).orElse(null);

        // Assert
        assertThat(updatedRoom.getName()).isEqualTo("neben Mensa");
    }

    @Test
    @DisplayName("Test deleteRoom")
    void testDeleteRoom() {
        // Arrange
        Long roomId = createAndAddRoom("neben Café");
        Room room = roomRepository.findById(roomId).orElse(null);

        // Act
        roomRepository.delete(room);
        List<Room> allRooms = roomRepository.findAll();

        // Assert
        assertThat(allRooms).doesNotContain(room);
    }
    @Test
    @DisplayName("Test deleteRoomById")
    void testDeleteRoombById() {
        // Arrange
        Long roomId = createAndAddRoom("neben Café");
        Room room = roomRepository.findById(roomId).orElse(null);

        // Act
        roomRepository.delete(roomRepository.findById(roomId).orElse(null));
        List<Room> allRooms = roomRepository.findAll();

        // Assert
        assertThat(allRooms).doesNotContain(room);
    }

    @Test
    @DisplayName("Test updateArbeitsplatz")
    void testUpdateArbeitsplatz() {
        // Arrange
        Long arbeitsplatzId = generateArbeitsplatzId();
        Arbeitsplatz retrievedArbeitsplatz = arbeitsplatzService.getArbeitsplatzById(arbeitsplatzId);
        retrievedArbeitsplatz.setName("UpdatedArbeitsplatz");

        // Act
        arbeitsplatzService.updateArbeitsplatz(retrievedArbeitsplatz);
        Arbeitsplatz updatedArbeitsplatz = arbeitsplatzService.getArbeitsplatzById(arbeitsplatzId);

        // Assert
        assertThat(updatedArbeitsplatz.getName()).isEqualTo("UpdatedArbeitsplatz");
    }

    @Test
    @DisplayName("Test addRoomAndReturnID")
    void testAddRoomAndReturnID() {
        // Arrange & Act
        Long roomId = createAndAddRoom("neben Café");

        // Assert
        assertThat(roomId).isNotNull();
    }

    @Test
    @DisplayName("Test deleteArbeitsplatz")
    void testDeleteArbeitsplatz() {
        // Arrange
        Long arbeitsplatzId = generateArbeitsplatzId();
        Arbeitsplatz retrievedArbeitsplatz = arbeitsplatzService.getArbeitsplatzById(arbeitsplatzId);

        // Act
        arbeitsplatzService.deleteArbeitsplatz(retrievedArbeitsplatz);
        List<Arbeitsplatz> allArbeitsplaetze = arbeitsplatzService.getAllArbeitsplaetze();

        // Assert
        assertThat(allArbeitsplaetze).doesNotContain(retrievedArbeitsplatz);
    }
    @Test
    @DisplayName("Test deleteAllArbeitsplatzeByRoomId")
    void testDeleteAllArbeitsplatzeByRoomId() {
        // Arrange
        Long room1Id = createAndAddRoom("neben Café");
        Arbeitsplatz arbeitsplatz1 = new Arbeitsplatz("TestArbeitsplatz1", room1Id);
        Arbeitsplatz arbeitsplatz2 = new Arbeitsplatz("TestArbeitsplatz2", room1Id);
        arbeitsplatzService.addPlatz(arbeitsplatz1);
        arbeitsplatzService.addPlatz(arbeitsplatz2);
        // Act
        arbeitsplatzService.deleteAllArbeitsplatzeByRoomId(room1Id);
        List<Arbeitsplatz> allArbeitsplaetze = arbeitsplatzService.getAllArbeitsplaetze();
        // Assert
        assertThat(allArbeitsplaetze).isEmpty();
    }

    @Test
    @DisplayName("Test  allArbeitsplatzeInRaumMitId")
    void testAllArbeitsplatzeInRaumMitId() {
        // Arrange
        Long room1Id = createAndAddRoom("neben Café");
        Arbeitsplatz arbeitsplatz1 = new Arbeitsplatz("TestArbeitsplatz1", room1Id);
        Arbeitsplatz arbeitsplatz2 = new Arbeitsplatz("TestArbeitsplatz2", room1Id);
        arbeitsplatzService.addPlatz(arbeitsplatz1);
        arbeitsplatzService.addPlatz(arbeitsplatz2);
        // Act
        List<Arbeitsplatz> allArbeitsplatzeInRaum = arbeitsplatzService.allArbeitsplatzeInRaumMitId(room1Id);
        // Assert
        assertThat(allArbeitsplatzeInRaum).contains(arbeitsplatz1, arbeitsplatz2);
    }
    @Test
    @DisplayName("Test  deleteArbeitsplatzById")
    void testDeleteArbeitsplatzById() {
        // Arrange
        Long arbeitsplatzId = generateArbeitsplatzId();

        // Act
        arbeitsplatzService.deleteArbeitsplatzById(arbeitsplatzId);
        Arbeitsplatz deletedArbeitsplatz = arbeitsplatzService.getArbeitsplatzById(arbeitsplatzId);
        // Assert
        assertThat(deletedArbeitsplatz).isNull();
    }

    @Test
    @DisplayName("Wenn ein reservation existiert , wird  True gegeben ")
    void testReservationExists(){
        //Arrange
        Long arbeitsplatzId = generateArbeitsplatzId();
        ReservationFormular reservation = new ReservationFormular(
               DATE,
               START_TIME,
               END_TIME,
                arbeitsplatzId);

        //Act
        reservationService.saveReservation(reservation);
        //Assert
        assertThat(reservationService.checkReservationExists(new ReservationFormular(
                DATE,
                START_TIME,
                END_TIME,
                arbeitsplatzId)))
                .isTrue();
    }

    @Test
    @DisplayName("Wenn ein reservation  nicht existiert , wird  false  gegeben ")
    void testReservationExists2(){
        //Arrange
        Long arbeitsplatzId = generateArbeitsplatzId();
        ReservationFormular reservation = new ReservationFormular(
                DATE,
                START_TIME,
                END_TIME,
                arbeitsplatzId);

        //Act
        reservationService.saveReservation(reservation);
        //Assert
        assertThat(reservationService.checkReservationExists(new ReservationFormular(
                DATE.plusDays(2),
                START_TIME,
                END_TIME,
                arbeitsplatzId)))
                .isFalse();
    }

    @Test
    @DisplayName("Wenn ein reservation  nicht existiert , wird gespeichert")
    void testSaveReservation(){
        //Arrange
        Long arbeitsplatzId = generateArbeitsplatzId();
        ReservationFormular reservation = new ReservationFormular(
                DATE,
                START_TIME,
                END_TIME,
                arbeitsplatzId);

        //Act
        reservationService.saveReservation(reservation);
        //Assert
        assertThat(reservationService.getAllReservation()).contains(reservation);
    }

    @Test
    @DisplayName("Deleting a Reservation")
    void deleteReservation(){
        Long arbeitsplatzId = generateArbeitsplatzId();
        ReservationFormular reservation1 = new ReservationFormular(
                DATE,
                START_TIME,
                END_TIME,
                arbeitsplatzId);
        ReservationFormular reservation2 = new ReservationFormular(
                DATE.plusDays(1),
                START_TIME,
                END_TIME,
                arbeitsplatzId);
        reservationService.saveReservation(reservation1);
        reservationService.saveReservation(reservation2);
        //Act
        reservationService.deleteReservationById(reservation1.getReservationId());

        //Assert
        assertThat(reservationService.getAllReservation()).doesNotContain(reservation1).contains(reservation2);

    }

    @Test
    void allowsReservationStartingWhenExistingOneEnds() {
        Long workspaceId = generateArbeitsplatzId();

        reservationService.saveReservation(new ReservationFormular(
                DATE,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                workspaceId
        ));

        boolean overlaps = reservationService.checkReservationExists(
                new ReservationFormular(
                        DATE,
                        LocalTime.of(11, 0),
                        LocalTime.of(12, 0),
                        workspaceId
                )
        );

        assertThat(overlaps).isFalse();
    }

    @Test
    void detectsPartiallyOverlappingReservation() {
        Long workspaceId = generateArbeitsplatzId();

        reservationService.saveReservation(new ReservationFormular(
                DATE,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                workspaceId
        ));

        boolean overlaps = reservationService.checkReservationExists(
                new ReservationFormular(
                        DATE,
                        LocalTime.of(10, 30),
                        LocalTime.of(11, 30),
                        workspaceId
                )
        );

        assertThat(overlaps).isTrue();
    }

    @Test
    void searchIncludesAvailableWorkspaceWithoutEquipment() {
        Long workspaceId = generateArbeitsplatzId();
        List<Arbeitsplatz> results = arbeitsplatzService.findAvailableArbeitsplatz(
                DATE, LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertThat(results).extracting(Arbeitsplatz::getArbeitsplatzId).contains(workspaceId);
    }

    @Test
    void searchExcludesOverlappingBookingButAllowsBackToBackBooking() {
        Long workspaceId = generateArbeitsplatzId();
        reservationService.saveReservation(new ReservationFormular(
                DATE, LocalTime.of(10, 0), LocalTime.of(11, 0), workspaceId));
        assertThat(arbeitsplatzService.findAvailableArbeitsplatz(
                DATE, LocalTime.of(10, 30), LocalTime.of(11, 30)))
                .extracting(Arbeitsplatz::getArbeitsplatzId).doesNotContain(workspaceId);
        assertThat(arbeitsplatzService.findAvailableArbeitsplatz(
                DATE, LocalTime.of(11, 0), LocalTime.of(12, 0)))
                .extracting(Arbeitsplatz::getArbeitsplatzId).contains(workspaceId);
    }







}
