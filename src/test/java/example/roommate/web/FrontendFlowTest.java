package example.roommate.web;

import example.roommate.Application.Service.*;
import example.roommate.Domain.model.Arbeitsplatz.Arbeitsplatz;
import example.roommate.Domain.model.Arbeitsplatz.Ausstatung;
import example.roommate.Domain.model.ReservationFormular;
import example.roommate.Domain.model.Room.Room;
import example.roommate.Web.AdminController;
import example.roommate.Web.WebController;
import example.roommate.Web.Security.MethodSecurityConfiguration;
import example.roommate.Web.Security.WebSecurityConfiguration;
import example.roommate.helper.WithMockOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({WebController.class, AdminController.class})
@Import({WebSecurityConfiguration.class, MethodSecurityConfiguration.class})
@WithMockOAuth2User(login="Alex", roles={"USER", "ADMIN"})
class FrontendFlowTest {
    @Autowired MockMvc mvc;
    @MockBean RoomService roomService;
    @MockBean ArbeitsplatzService arbeitsplatzService;
    @MockBean PersonService personService;
    @MockBean ReservationService reservationService;
    Room room;
    Arbeitsplatz desk;
    LocalDate date = LocalDate.now().plusDays(1);

    @BeforeEach
    void setUp() {
        room = new Room(1L, "The Garden Room", UUID.randomUUID());
        desk = new Arbeitsplatz("Window desk", 1L);
        desk.setArbeitsplatzId(5L);
        desk.addAusstatung(new Ausstatung("hdmi"));
        desk.addAusstatung(new Ausstatung("usbc"));
        when(roomService.getAllRooms()).thenReturn(List.of(room,
                new Room(2L, "The Studio", UUID.randomUUID()), new Room(3L, "Quiet Corner", UUID.randomUUID())));
        when(roomService.getRoomById(1L)).thenReturn(room);
        when(arbeitsplatzService.allArbeitsplatzeInRaumMitId(1L)).thenReturn(List.of(desk));
        when(arbeitsplatzService.getArbeitsplatzById(5L)).thenReturn(desk);
        when(arbeitsplatzService.findAvailableArbeitsplatz(any(), any(), any())).thenReturn(List.of(desk));
        when(arbeitsplatzService.filterArbeitsplatzByEquipment(anyList(), anyList())).thenReturn(List.of(desk));
        when(reservationService.getAllReservation()).thenReturn(List.of(new ReservationFormular(
                101L, date, LocalTime.of(10,0), LocalTime.of(11,0), 5L)));
        doAnswer(invocation -> {
            ReservationFormular booking = invocation.getArgument(0);
            BindingResult errors = invocation.getArgument(1);
            if (!booking.getEndTime().isAfter(booking.getStartTime())) {
                errors.rejectValue("endTime", "invalid", "End time must be after start time");
            }
            return null;
        }).when(reservationService).validateTime(any(), any());
    }

    private void snapshot(String name, MvcResult result) throws Exception {
        Path dir = Path.of("build/ui-preview");
        Files.createDirectories(dir.resolve("css"));
        Files.createDirectories(dir.resolve("js"));
        Files.writeString(dir.resolve(name + ".html"), result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        Files.copy(Path.of("src/main/resources/static/css/roommate.css"), dir.resolve("css/roommate.css"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Path.of("src/main/resources/static/js/roommate.js"), dir.resolve("js/roommate.js"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void rendersAllUserAndAdminPagesWithRealTemplates() throws Exception {
        String[][] pages = {{"/", "home"}, {"/chooseroom", "rooms"}, {"/search", "search"},
                {"/chooseplatz/1", "booking"}, {"/confirm", "confirm"}, {"/roomoverview", "admin"},
                {"/addroom", "add-room"}, {"/addarbeitsplatz/1", "add-desk"}, {"/addaustattung/5", "add-equipment"},
                {"/modifyroom/1", "workspaces"}, {"/modifyarbeitsplatz/5", "equipment"}, {"/allreservation", "reservations"}, {"/login", "login"}};
        for (String[] page : pages) {
            MvcResult result = mvc.perform(get(page[0])).andExpect(status().isOk()).andReturn();
            assertThat(result.getResponse().getContentAsString()).contains("/css/roommate.css", "RoomMate");
            snapshot(page[1], result);
        }
    }

    @Test
    void successfulBookingSavesAndRedirectsToConfirmation() throws Exception {
        mvc.perform(post("/chooseplatz").with(csrf()).param("roomId", "1").param("arbeitsplatzId", "5")
                .param("tag", date.toString()).param("startTime", "10:00").param("endTime", "11:00"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/confirm"));
        verify(reservationService).saveReservation(argThat(r -> r.getArbeitsplatzId().equals(5L)
                && r.getStartTime().equals(LocalTime.of(10, 0))));
        mvc.perform(get("/confirm")).andExpect(status().isOk()).andExpect(view().name("confirm"));
    }

    @Test
    void missingTimesShowFieldErrorsWithoutComparingOrSaving() throws Exception {
        MvcResult result = mvc.perform(post("/chooseplatz").with(csrf()).param("roomId", "1")
                .param("arbeitsplatzId", "5").param("tag", date.toString()))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("reservation", "startTime", "endTime"))
                .andExpect(view().name("platzwählen")).andReturn();
        verify(reservationService, never()).validateTime(any(), any());
        verify(reservationService, never()).saveReservation(any());
        snapshot("booking-errors", result);
    }

    @Test
    void malformedDateShowsFormRatherThanCrashing() throws Exception {
        mvc.perform(post("/chooseplatz").with(csrf()).param("roomId", "1").param("arbeitsplatzId", "5")
                .param("tag", "not-a-date").param("startTime", "10:00").param("endTime", "11:00"))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("reservation", "tag"));
        verify(reservationService, never()).saveReservation(any());
    }

    @Test
    void equalTimesShowFormErrorAndNeverSave() throws Exception {
        mvc.perform(post("/chooseplatz").with(csrf()).param("roomId", "1").param("arbeitsplatzId", "5")
                .param("tag", date.toString()).param("startTime", "10:00").param("endTime", "10:00"))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("reservation", "endTime"));
        verify(reservationService, never()).saveReservation(any());
    }

    @Test
    void overlappingBookingShowsFeedbackAndNeverSaves() throws Exception {
        when(reservationService.checkReservationExists(any())).thenReturn(true);
        mvc.perform(post("/chooseplatz").with(csrf()).param("roomId", "1").param("arbeitsplatzId", "5")
                .param("tag", date.toString()).param("startTime", "10:00").param("endTime", "11:00"))
                .andExpect(status().isOk()).andExpect(model().attribute("error", "This place is already booked for the selected time"));
        verify(reservationService, never()).saveReservation(any());
    }

    @Test
    void searchBindsChosenTimesAndPassesThemToBookingLink() throws Exception {
        MvcResult result = mvc.perform(post("/search").with(csrf()).param("date", date.toString())
                .param("timeFrom", "10:00").param("timeTo", "11:00").param("equipment", "hdmi"))
                .andExpect(status().isOk()).andExpect(view().name("suchenErgebnisse")).andReturn();
        verify(arbeitsplatzService).findAvailableArbeitsplatz(date, LocalTime.of(10,0), LocalTime.of(11,0));
        assertThat(result.getResponse().getContentAsString()).contains("tag=", "startTime=", "endTime=", "arbeitsplatzId=5");
        snapshot("results", result);
        mvc.perform(get("/chooseplatz/1").param("arbeitsplatzId", "5").param("tag", date.toString())
                .param("startTime", "10:00").param("endTime", "11:00"))
                .andExpect(status().isOk()).andExpect(model().attribute("reservation", org.hamcrest.Matchers.hasProperty("startTime", org.hamcrest.Matchers.is(LocalTime.of(10,0)))));
    }

    @Test
    void invalidSearchRangeStaysOnSearchForm() throws Exception {
        mvc.perform(post("/search").with(csrf()).param("date", date.toString()).param("timeFrom", "11:00").param("timeTo", "10:00"))
                .andExpect(status().isOk()).andExpect(view().name("suchen")).andExpect(model().attributeExists("error"));
        verify(arbeitsplatzService, never()).findAvailableArbeitsplatz(any(), any(), any());
    }

    @Test
    void equipmentActionsUseTheWorkspaceId() throws Exception {
        String html = mvc.perform(get("/modifyarbeitsplatz/5")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(html).contains("/platz/5/ausstatung/hdmi", "/addaustattung/5");
    }

    @Test
    void emptyRoomsAndSearchResultsRenderUsefulStates() throws Exception {
        when(roomService.getAllRooms()).thenReturn(List.of());
        MvcResult rooms = mvc.perform(get("/chooseroom")).andExpect(status().isOk()).andReturn();
        assertThat(rooms.getResponse().getContentAsString()).contains("No rooms just yet");
        snapshot("rooms-empty", rooms);
        when(arbeitsplatzService.findAvailableArbeitsplatz(any(), any(), any())).thenReturn(List.of());
        MvcResult results = mvc.perform(post("/search").with(csrf()).param("date", date.toString()).param("timeFrom", "10:00").param("timeTo", "11:00"))
                .andExpect(status().isOk()).andReturn();
        assertThat(results.getResponse().getContentAsString()).contains("No desks match this time");
        snapshot("results-empty", results);
    }

    @Test
    void blankAdminNamesAreNotSaved() throws Exception {
        mvc.perform(post("/addroom").with(csrf()).param("roomName", "  ")).andExpect(status().isOk()).andExpect(view().name("addraum"));
        verify(roomService, never()).addRoomAndReturnID(any());
        mvc.perform(post("/addarbeitsplatz/1").with(csrf()).param("arbeitsplatzName", "  ")).andExpect(status().isOk()).andExpect(view().name("addarbeitsplatz"));
        verify(arbeitsplatzService, never()).addPlatzAndReturnID(any());
    }

    @Test
    void workspaceFromAnotherRoomCannotBeBooked() throws Exception {
        mvc.perform(post("/chooseplatz").with(csrf()).param("roomId", "1").param("arbeitsplatzId", "999")
                .param("tag", date.toString()).param("startTime", "10:00").param("endTime", "11:00"))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("reservation", "arbeitsplatzId"));
        verify(reservationService, never()).saveReservation(any());
    }

    @Test
    @org.springframework.security.test.context.support.WithAnonymousUser
    void publicHomepageAndLoginUseLocalStylesAndOfferGithubSignIn() throws Exception {
        for (String path : List.of("/", "/login")) {
            String html = mvc.perform(get(path)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            assertThat(html).contains("/oauth2/authorization/github", "/css/roommate.css").doesNotContain("Manage spaces", "Sign out");
        }
        mvc.perform(get("/css/roommate.css")).andExpect(status().isOk());
        mvc.perform(get("/js/roommate.js")).andExpect(status().isOk());
    }

    @Test
    @WithMockOAuth2User(roles={"USER"})
    void normalUserDoesNotSeeAdminNavigationAndCannotAccessAdmin() throws Exception {
        String html = mvc.perform(get("/chooseroom")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(html).doesNotContain("Manage spaces");
        mvc.perform(get("/roomoverview")).andExpect(status().isForbidden());
    }

    @Test
    void cancelReservationPostsToCorrectEndpoint() throws Exception {
        String html = mvc.perform(get("/allreservation")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(html).contains("/deleteReservation/101", "name=\"_csrf\"");
        mvc.perform(post("/deleteReservation/101").with(csrf())).andExpect(redirectedUrl("/allreservation"));
        verify(reservationService).deleteReservationById(101L);
    }
}
