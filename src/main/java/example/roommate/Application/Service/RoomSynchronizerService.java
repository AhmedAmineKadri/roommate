package example.roommate.Application.Service;

import example.roommate.Domain.model.Room.Room;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@ConditionalOnProperty(name = "roommate.keymaster.enabled", havingValue = "true", matchIfMissing = true)
public class RoomSynchronizerService {

    private final RoomService roomService;

    public RoomSynchronizerService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Scheduled(fixedDelay = 10000)
    public void fetchEvents() {
        List<Room> rooms = WebClient.create()
                .get()
                .uri(
                        uriBuilder -> uriBuilder
                                .scheme("http")
                                .host("localhost")
                                .port(3000)
                                .path("room")
                                .build()
                )
                .retrieve()
                .bodyToFlux(Room.class)
                .collectList()
                .block(Duration.of(8, ChronoUnit.SECONDS));
        for (Room room : rooms) {
            processEvent(room);
        }
    }

    private void processEvent(Room room) {
        if(roomService.findByUUID(room.getId()).isEmpty()){
            roomService.addRoom(room);
        }
    }
}
