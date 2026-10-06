package example.roommate.Web;

import example.roommate.Application.Service.ReservationService;
import example.roommate.Domain.model.Arbeitsplatz.Arbeitsplatz;
import example.roommate.Domain.model.Arbeitsplatz.Ausstatung;
import example.roommate.Domain.model.ReservationFormular;
import example.roommate.Domain.model.Room.Room;
import example.roommate.Application.Service.ArbeitsplatzService;
import example.roommate.Application.Service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@OnlyAdmin
@Controller
public class AdminController {
    private final RoomService roomService;
    private final ArbeitsplatzService arbeitsplatzService;

    private final ReservationService reservationService;


    public AdminController(RoomService roomService,
                           ArbeitsplatzService arbeitsplatzService,
                           ReservationService reservationService) {
        this.roomService = roomService;
        this.arbeitsplatzService = arbeitsplatzService;
        this.reservationService = reservationService;
    }


    @GetMapping("/addroom")
    public  String addRoomMenu(){
        return "addraum";
    }


    @PostMapping("/addroom")
    public String addRoom(@RequestParam String roomName,
                          Model model) {
        if (roomName.isBlank() || roomName.length() > 300) {
            model.addAttribute("error", "Enter a room name between 1 and 300 characters.");
            model.addAttribute("name", roomName);
            return "addraum";
        }
        Room room = new Room(roomName.trim());
        Long id = roomService.addRoomAndReturnID(room);
        model.addAttribute("roomId",id);
        return "redirect:/addarbeitsplatz/"+id;
    }


    @GetMapping("/addarbeitsplatz/{roomId}")
    public  String addArbeitplatzMenu(Model model , @PathVariable Long roomId){
        model.addAttribute("roomId", roomId);
        return "addarbeitsplatz";
    }

    @PostMapping("/addarbeitsplatz/{roomId}")
    public String addArbeitsplatz(@RequestParam String arbeitsplatzName, Model model,
                                  @PathVariable Long roomId) {
        if (arbeitsplatzName.isBlank() || arbeitsplatzName.length() > 300) {
            model.addAttribute("roomId", roomId);
            model.addAttribute("name", arbeitsplatzName);
            model.addAttribute("error", "Enter a workspace name between 1 and 300 characters.");
            return "addarbeitsplatz";
        }
        Arbeitsplatz arbeitsplatz = new Arbeitsplatz(arbeitsplatzName.trim(), roomId);
        Long id = arbeitsplatzService.addPlatzAndReturnID(arbeitsplatz);
        model.addAttribute("arbeitsplatzId",id);
        return "redirect:/addaustattung/"+id;

    }

    @GetMapping("/addaustattung/{arbeitsplatzId}")
    public String addAustattungMenu(Model model, @PathVariable Long arbeitsplatzId) {
        // to show schon existierte Ausstatungs
        Arbeitsplatz arbeitsplatz = arbeitsplatzService
                .getArbeitsplatzById(arbeitsplatzId);
        if (arbeitsplatz == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found");
        List<Ausstatung> ausstatungs = arbeitsplatz.getAusstatungs();
        model.addAttribute("ausstatungs", ausstatungs);
        model.addAttribute("arbeitsplatzId", arbeitsplatzId);

        return "addausstattung";
    }

    @PostMapping("/addaustattung/{arbeitsplatzId}")
    public String  addAustattung(@RequestParam String austattungName,
                                 @PathVariable Long arbeitsplatzId, Model model) {
        Arbeitsplatz arbeitsplatz = arbeitsplatzService.getArbeitsplatzById(arbeitsplatzId);
        if (arbeitsplatz == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found");
        if (austattungName.isBlank() || austattungName.length() > 300) {
            model.addAttribute("error", "Enter an equipment name between 1 and 300 characters.");
            model.addAttribute("arbeitsplatzId", arbeitsplatzId);
            model.addAttribute("ausstatungs", arbeitsplatz.getAusstatungs());
            return "addausstattung";
        }
        arbeitsplatz.addAusstatung(new Ausstatung(austattungName.trim().toLowerCase()));
        arbeitsplatzService.updateArbeitsplatz(arbeitsplatz);
        return "redirect:/addaustattung/"+arbeitsplatzId;
    }

    @GetMapping("/roomoverview")
    public String roomOverview(Model model) {
        List<Room> rooms = roomService.getAllRooms();
        model.addAttribute("rooms", rooms);
        return "roomoverview";
    }
        //no Longer needed
//    @PostMapping("/deleteroom/{roomId}")
//    public String deleteRoom(@PathVariable Long roomId) {
//        System.out.println(roomId);
//        arbeitsplatzService.deleteAllArbeitsplatzeByRoomId(roomId);
//        roomService.deleteRoombyId(roomId);
//        return "redirect:/roomoverview";
//    }

    @GetMapping("/modifyroom/{roomId}")
    public String modifyRoom(@PathVariable Long roomId, Model  model) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        model.addAttribute("room", room);
        model.addAttribute("roomId",roomId);
        List<Arbeitsplatz> arbeitsplatze = arbeitsplatzService.allArbeitsplatzeInRaumMitId(roomId);
        model.addAttribute("arbeitsplatze",arbeitsplatze);
        return "arbeitsplatzeübersichtAdmin";
    }

    @PostMapping("/room/{roomId}/platz/{arbeitsplatzId}")
    public String deleteArbeitsplatz(@PathVariable Long arbeitsplatzId,
                                     @PathVariable Long roomId) {
        arbeitsplatzService.deleteArbeitsplatzById(arbeitsplatzId);
        return "redirect:/modifyroom/" + roomId;
    }

    @GetMapping("/modifyarbeitsplatz/{arbeitsplatzId}")
    public String modifyArbeitsplatz(@PathVariable Long arbeitsplatzId, Model model){
        Arbeitsplatz arbeitsplatz = arbeitsplatzService.getArbeitsplatzById(arbeitsplatzId);
        model.addAttribute("arbeitsplatz", arbeitsplatz);
        if (arbeitsplatz == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found");
        model.addAttribute("arbeitsplatzId", arbeitsplatzId);
        List<Ausstatung> ausstatungList=arbeitsplatz.getAusstatungs();
        model.addAttribute("ausstatungList",ausstatungList);
        return "ausstatungübersichtAdmin";
    }

    @PostMapping("/platz/{arbeitsplatzId}/ausstatung/{ausstatungName}")
    public String deleteAusstattung(@PathVariable String ausstatungName,
                                    @PathVariable Long arbeitsplatzId){
        Arbeitsplatz arbeitsplatz = arbeitsplatzService.deleteAusstatung(ausstatungName, arbeitsplatzId);
        arbeitsplatzService.updateArbeitsplatz(arbeitsplatz);
        return "redirect:/modifyarbeitsplatz/" + arbeitsplatzId;
    }

    @GetMapping("/allreservation")
    public String showAllReservation(Model model){
        List<ReservationFormular> allReservation = reservationService.getAllReservation();
        model.addAttribute("allreservation",allReservation);
        return "reservationuebersicht";
    }

    @PostMapping("deleteReservation/{reservationId}")
    public String deleteReservation (@PathVariable Long reservationId){
        reservationService.deleteReservationById(reservationId);
        return "redirect:/allreservation";
    }




}



