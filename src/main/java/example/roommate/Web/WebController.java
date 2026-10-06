package example.roommate.Web;

import example.roommate.Application.Service.ArbeitsplatzService;
import example.roommate.Application.Service.PersonService;
import example.roommate.Application.Service.ReservationService;
import example.roommate.Application.Service.RoomService;
import example.roommate.Domain.model.Arbeitsplatz.Arbeitsplatz;
import example.roommate.Domain.model.ReservationFormular;
import example.roommate.Domain.model.Room.Room;

import jakarta.validation.Valid;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@SessionAttributes("roomId")
public class WebController {
    private final RoomService roomService;
    private final ArbeitsplatzService arbeitsplatzService;
    private final PersonService personService;
    private final ReservationService reservationService;
    public WebController(RoomService roomService, ArbeitsplatzService arbeitsplatzService,
                         PersonService personService, ReservationService reservationService) {
        this.roomService = roomService;
        this.arbeitsplatzService = arbeitsplatzService;
        this.personService=personService;
        this.reservationService = reservationService;
    }
    @GetMapping("/")
    public String init(OAuth2AuthenticationToken auth, Model model ){
        if (auth != null) {
            String username = auth.getPrincipal().getAttribute("login");
            Integer id = auth.getPrincipal().getAttribute("id");
            model.addAttribute("username", username);
            personService.getOrCreatePerson(id, username);
        }
        return "initPage";
    }


    @GetMapping("/chooseroom")
    public String chooseRoom(Model model){
        List<Room> allRooms = roomService.getAllRooms();
        model.addAttribute("allRooms",allRooms);
        return "raumübersicht";
    }
    @PostMapping("/chooseroom")
    public String chooseraum(@RequestParam Long selectedRoom){

        return "redirect:/chooseplatz/"+selectedRoom;
    }
    @GetMapping("/chooseplatz/{roomId}")
    public String chooseArbeitsplatz(Model model,
                                     @PathVariable Long roomId,
                                     @ModelAttribute("reservation") ReservationFormular reservationDetails) {

        model.addAttribute("roomId",roomId);
        List<Arbeitsplatz> arbeitsplatzs = arbeitsplatzService.allArbeitsplatzeInRaumMitId(roomId);
        model.addAttribute("arbeitsplatzs", arbeitsplatzs);
        model.addAttribute("arbeitsplatzId", reservationDetails.getArbeitsplatzId());
        model.addAttribute("reservation", reservationDetails);
        return "platzwählen";
    }

    @PostMapping("/chooseplatz")
    public String chooseArbeitsplatzPost(@ModelAttribute("reservation") @Valid ReservationFormular reservation,
                                         BindingResult bindingResult,
                                         @RequestParam(value = "roomId", required = false) Long submittedRoomId,
                                         Model model) {
        Long roomId = submittedRoomId != null ? submittedRoomId : (Long) model.getAttribute("roomId");
        if (roomId == null) {
            return "redirect:/chooseroom";
        }
        model.addAttribute("roomId", roomId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("arbeitsplatzId", reservation.getArbeitsplatzId());
            model.addAttribute("reservation", reservation);
            return displayArbeitsplatzs(
                    model,
                    (Long) model.getAttribute("roomId"),
                    "Invalid reservation details, Please try again"
            );
        }

        reservationService.validateTime(reservation, bindingResult);
        boolean workspaceInRoom = arbeitsplatzService.allArbeitsplatzeInRaumMitId(roomId).stream()
                .anyMatch(place -> place.getArbeitsplatzId().equals(reservation.getArbeitsplatzId()));
        if (!workspaceInRoom) {
            bindingResult.rejectValue("arbeitsplatzId", "invalid", "Choose a workspace in this room.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("arbeitsplatzId", reservation.getArbeitsplatzId());
            model.addAttribute("reservation",reservation);
            return displayArbeitsplatzs(model, (Long) model.getAttribute("roomId"), "Invalid reservation details, Please try again");
        }
        if (reservationService.checkReservationExists(reservation)) {
            model.addAttribute("arbeitsplatzId", reservation.getArbeitsplatzId());
            model.addAttribute("reservation",reservation);
            return displayArbeitsplatzs(model, (Long) model.getAttribute("roomId"), "This place is already booked for the selected time");
        }
        reservationService.saveReservation(reservation);
        return "redirect:/confirm";
    }

    @GetMapping("/confirm")
    public String confirmation() {
        return "confirm";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }




    @GetMapping("/search")
    public String searchMenu(Model model){
        WantedPeriod period = new WantedPeriod();
        java.time.LocalDateTime start = java.time.LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1);
        if (start.getHour() >= 23) {
            start = start.toLocalDate().plusDays(1).atTime(9, 0);
        }
        period.setDate(start.toLocalDate());
        period.setTimeFrom(start.toLocalTime());
        period.setTimeTo(start.toLocalTime().plusHours(1));
        model.addAttribute("period", period);
        return "suchen";
    }
    @PostMapping("/search")
    public String search(
                         @RequestParam(value = "equipment", required = false) List<String> equipment,
                         @ModelAttribute("period") WantedPeriod wantedPeriod,
                         BindingResult bindingResult,
                         Model model)
    {
        model.addAttribute("equipment", equipment == null ? List.of() : equipment);
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Enter a valid date and time.");
            return "suchen";
        }
        wantedPeriod.date = defaultToDate(wantedPeriod.date);
        wantedPeriod.timeFrom = defaultToStartTime(wantedPeriod.timeFrom);
        wantedPeriod.timeTo = wantedPeriod.timeTo == null
                ? wantedPeriod.timeFrom.plusHours(1) : wantedPeriod.timeTo;
        if (wantedPeriod.date.isBefore(LocalDate.now()) || !wantedPeriod.timeTo.isAfter(wantedPeriod.timeFrom)) {
            model.addAttribute("error", "Choose today or a future date, with an end time after the start time.");
            return "suchen";
        }
        List<Arbeitsplatz> availableArbeitsplatz = arbeitsplatzService.findAvailableArbeitsplatz(wantedPeriod.date,
                                                                                                 wantedPeriod.timeFrom,
                                                                                                 wantedPeriod.timeTo);
        model.addAttribute("datum", wantedPeriod.date);
        model.addAttribute("startzeit", wantedPeriod.timeFrom);
        model.addAttribute("endzeit", wantedPeriod.timeTo);
        if(equipment!=null){
            availableArbeitsplatz = arbeitsplatzService.filterArbeitsplatzByEquipment(availableArbeitsplatz, equipment);
        }
        model.addAttribute("availableArbeitsplatz",availableArbeitsplatz);
        return "suchenErgebnisse";
    }
    private LocalDate defaultToDate(LocalDate date){
        return (date == null) ? LocalDate.now() : date;
    }

    private LocalTime defaultToStartTime(LocalTime timeFrom) {
        return (timeFrom == null) ? LocalTime.now().truncatedTo(ChronoUnit.MINUTES) : timeFrom;
    }

    private String displayArbeitsplatzs(Model model, Long selectedRoom, String error) {
        model.addAttribute("error", error);
        List<Arbeitsplatz> arbeitsplatzs = arbeitsplatzService.allArbeitsplatzeInRaumMitId(selectedRoom);
        model.addAttribute("arbeitsplatzs", arbeitsplatzs);
        return "platzwählen";
    }




}
