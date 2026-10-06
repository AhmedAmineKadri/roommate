package example.roommate.DataBase;

import org.springframework.data.annotation.Id;

import java.util.Objects;
import java.util.UUID;

public record RoomDto(@Id Long roomId, String name, UUID id) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomDto roomDto = (RoomDto) o;
        return Objects.equals(roomId, roomDto.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }
}
