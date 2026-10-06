package example.roommate.DataBase;

import example.roommate.Domain.model.Synchronizer.RoomKeyResponse;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomDAO extends CrudRepository<RoomDto, Long> {
    @Override
    Iterable<RoomDto> findAll();
    @Query("select room_dto.name from room_dto")
    List<String> getRoomsNames();

   @Query("select * from room_dto where room_dto.id = :id")
    Optional<RoomDto> findById(@Param("id") UUID id);

   @Query("select k.id as key ,r.id as room,r.name as raum, k.owner as owner FROM room_dto r INNER JOIN key k ON r.room_id = k.key_id")
    List<RoomKeyResponse> findRoomsAndKeys();
}
