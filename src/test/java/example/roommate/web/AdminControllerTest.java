package example.roommate.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import example.roommate.Application.Service.ReservationService;
import example.roommate.Domain.model.Arbeitsplatz.Arbeitsplatz;
import example.roommate.Domain.model.ReservationFormular;
import example.roommate.Domain.model.Room.Room;
import example.roommate.Application.Service.ArbeitsplatzService;
import example.roommate.Application.Service.RoomService;
import example.roommate.Web.AdminController;
import example.roommate.Web.Security.MethodSecurityConfiguration;
import example.roommate.Web.Security.WebSecurityConfiguration;
import example.roommate.helper.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockOAuth2User
@WebMvcTest(AdminController.class)
@Import({WebSecurityConfiguration.class, MethodSecurityConfiguration.class})
public class AdminControllerTest {
    public static final LocalDate DATE = LocalDate.of(2024, 02, 21);
    public static final LocalTime START_TIME = LocalTime.of(14, 30);
    public static final LocalTime END_TIME = LocalTime.of(16, 30);

    @Autowired
    MockMvc mvc;

    @MockBean
    RoomService roomService;
    @MockBean
    ArbeitsplatzService arbeitsplatzService;

    @MockBean
    ReservationService reservationService;
//    @MockBean
//    AusstatungService  ausstatungService;

    @MockBean
    Room room;
    @MockBean
    Arbeitsplatz arbeitsplatz;

    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("admin can access admin page")
    void AdminWithRole() throws Exception {
        mvc.perform(get("/roomoverview"))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockOAuth2User(login = "name")
    @DisplayName("user cant access admin page")
    void UserAccessAdminPage() throws Exception {
        mvc.perform(get("/roomoverview"))
                .andExpect(status().isForbidden());
    }



    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("Get request for /addarbeitsplatz/{roomId}")
    void test_3() throws Exception {
        mvc.perform(get("/addarbeitsplatz/{roomId}", 1L))
                .andExpect(model().attributeExists("roomId"))
                .andExpect(status().isOk())
                .andExpect(view().name("addarbeitsplatz"));
    }

    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("Post request for /addarbeitsplatz/{roomId}")
    void test4() throws Exception {
        when(arbeitsplatzService.addPlatzAndReturnID(any())).thenReturn(2L);
        mvc.perform(post("/addarbeitsplatz/{roomId}", 1)
                        .param("arbeitsplatzName", "arbeitsplatz1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addaustattung/2"));
    }

    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("Get request for /addaustattung/{arbeitsplatzId}")
    void test_5() throws Exception {
        when(arbeitsplatzService.getArbeitsplatzById(any())).thenReturn(arbeitsplatz);
        mvc.perform(get("/addaustattung/{arbeitsplatzId}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("addausstattung"));
    }

    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("Post request for /addaustattung/{arbeitsplatzId}")
    void test6() throws Exception {

        when(arbeitsplatzService.getArbeitsplatzById(any())).thenReturn(arbeitsplatz);
        mvc.perform(post("/addaustattung/{arbeitsplatzId}", 1L).with(csrf())
                        .param("austattungName", "austattung1"))
                .andExpect(redirectedUrl("/addaustattung/1"));
        verify(arbeitsplatz).addAusstatung(any());
    }

    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("Get request for /addaustattung/{arbeitsplatzId}")
    void test_7() throws Exception {
        mvc.perform(get("/roomoverview"))
                .andExpect(status().isOk())
                .andExpect(view().name("roomoverview"));
    }

    //    @Test
//    @DisplayName("Post request for /deleteroom/{roomId}")
//    void test7() throws Exception{
//        mvc.perform(post("/deleteroom/{roomId}",1L).with(csrf()))
//                .andExpect(redirectedUrl("/roomoverview"));
//          verify(arbeitsplatzService).deleteAllArbeitsplatzeByRoomId(1L);
//          verify(roomService).deleteRoombyId(1L);
//    }
    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("Get request for /modifyroom/{roomId}")
    void test_8() throws Exception {

        when(room.getRoomId()).thenReturn(1L);
        when(roomService.getRoomById(any())).thenReturn(room);
        when(arbeitsplatzService.allArbeitsplatzeInRaumMitId(room.getRoomId())).thenReturn(List.of());
        mvc.perform(get("/modifyroom/{roomId}", 1L))
                .andExpect(view().name("arbeitsplatzeübersichtAdmin"))
                .andExpect(model().attributeExists("arbeitsplatze","roomId","room"));
    }

    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("Post Request for /room/{roomId}/platz/{arbeitsplatzId}")
    void test9() throws Exception {
        mvc.perform(post("/room/{roomId}/platz/{arbeitsplatzId}", 1L, 1L).with(csrf()))
                .andExpect(redirectedUrl("/modifyroom/1"));
        verify(arbeitsplatzService).deleteArbeitsplatzById(1L);
    }

    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("Get request for /modifyarbeitsplatz/{arbeitsplatzId}")
    void test_10() throws Exception {
        when(arbeitsplatzService.getArbeitsplatzById(any())).thenReturn(arbeitsplatz);
        when(arbeitsplatz.getAusstatungs()).thenReturn(List.of());
        mvc.perform(get("/modifyarbeitsplatz/{arbeitsplatzId}",1L))
                .andExpect(status().isOk())
                .andExpect(view().name("ausstatungübersichtAdmin"))
                .andExpect(model().attributeExists("ausstatungList","arbeitsplatz"));
        verify(arbeitsplatzService).getArbeitsplatzById(1L);
    }
    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("Post Request for /platz/{arbeitsplatzId}/ausstatung/{ausstatungName}")
    void test11() throws Exception {
       when(arbeitsplatzService.deleteAusstatung("A1",1L)).thenReturn(arbeitsplatz);
        mvc.perform(post("/platz/{arbeitsplatzId}/ausstatung/{ausstatungName}", 1L,"A1").with(csrf()))
                .andExpect(redirectedUrl("/modifyarbeitsplatz/1"));
        verify(arbeitsplatzService).updateArbeitsplatz(arbeitsplatz);
    }

    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("get Request for /allreservation works")
    void test12() throws Exception {
        //Arrange
        List<ReservationFormular> reservationList = List.of(new ReservationFormular(DATE,
                START_TIME,
                END_TIME,
                1L));
        when(reservationService.getAllReservation()).thenReturn(reservationList);
        //Act+ASSERT
        mvc.perform(get("/allreservation").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("reservationuebersicht"))
                .andExpect(model().attribute("allreservation",reservationList));
    }

}




