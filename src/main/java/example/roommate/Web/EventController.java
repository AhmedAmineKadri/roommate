package example.roommate.Web;

import example.roommate.Domain.model.Synchronizer.RoomKeyResponse;
import example.roommate.Application.Service.RoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class EventController {
    private final RoomService roomService;

    public EventController(RoomService roomService) {
        this.roomService = roomService;
    }
    @GetMapping("/api/access")
    public  List <RoomKeyResponse> verfuegbareRaueme() {
        return roomService.getAllRoomsWithKeys();
    }



}
