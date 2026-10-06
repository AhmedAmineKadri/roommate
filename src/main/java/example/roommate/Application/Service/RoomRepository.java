package example.roommate.Application.Service;

import example.roommate.Domain.model.Room.Room;
import example.roommate.Domain.model.Synchronizer.RoomKeyResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository {

    List<Room> findAll();

    List<String> getRoomsNames();

    Optional<Room> findById(Long id);

    Room save(Room room);

    void delete(Room room);

    List<RoomKeyResponse> findRoomsAndKeys();

    Optional<Room> findByUUIDId(UUID id);
}
