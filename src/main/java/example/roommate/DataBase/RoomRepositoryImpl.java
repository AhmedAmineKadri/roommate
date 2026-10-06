package example.roommate.DataBase;

import example.roommate.Application.Service.RoomRepository;
import example.roommate.Domain.model.Room.Room;
import example.roommate.Domain.model.Synchronizer.RoomKeyResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public class RoomRepositoryImpl implements RoomRepository {
    private final RoomDAO roomDAO;

    public RoomRepositoryImpl(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    private Room convertRoomDto(RoomDto roomDtos) {
        Room room = new Room();
        room.setRoomId(roomDtos.roomId());
        room.setName(roomDtos.name());
        room.setId(roomDtos.id());
        return room;
    }

    @Override
    public List<Room> findAll() {
        List<RoomDto> roomDtos = (List<RoomDto>) roomDAO.findAll();
        return roomDtos.stream().map(this::convertRoomDto).toList();
    }

    @Override
    public List<String> getRoomsNames() {
        return roomDAO.getRoomsNames();
    }

    @Override
    public Optional<Room> findById(Long id) {
        Optional<RoomDto> optionalRoomDto = roomDAO.findById(id);
        if (optionalRoomDto.isPresent()){
            return Optional.of(convertRoomDto(optionalRoomDto.orElse(null)));
        }
        return Optional.empty();
    }

    @Override
    public Room save(Room room) {
        RoomDto roomDto = convertRoom(room);
        RoomDto savedDto = roomDAO.save(roomDto);
        return convertRoomDto(savedDto);

    }

    private RoomDto convertRoom(Room room) {
        return new RoomDto(room.getRoomId(),room.getName(),room.getId());
    }

    @Override
    public void delete(Room room) {

        roomDAO.delete(convertRoom(room));
    }

    @Override
    public List<RoomKeyResponse> findRoomsAndKeys() {
        return roomDAO.findRoomsAndKeys();
    }

    @Override
    public Optional<Room> findByUUIDId(UUID id) {

        Optional<RoomDto> optionalRoomDto = roomDAO.findById(id);
        if (optionalRoomDto.isPresent()) {
            return Optional.of(convertRoomDto(optionalRoomDto.orElse(null)));
        }
        return Optional.empty();
    }
}
