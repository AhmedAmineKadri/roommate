package example.roommate.web;


import example.roommate.Application.Service.ArbeitsplatzService;
import example.roommate.Application.Service.PersonService;
import example.roommate.Application.Service.ReservationService;
import example.roommate.Application.Service.RoomService;
import example.roommate.Domain.model.ReservationFormular;
import example.roommate.Web.WebController;
import example.roommate.helper.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(WebController.class)

public class WebControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    RoomService roomService;
    @MockBean
    ArbeitsplatzService arbeitsplatzService;
//    @MockBean
//    AusstatungService ausstatungService;
    @MockBean
ReservationService reservationService;
    @MockBean
    PersonService personService;

    @MockBean
    ReservationFormular reservation;

    @Test
    @DisplayName("Status beim Starten der Webanwendung ohne authentifizierung ist 302")
    void chooseRoomWithoutLogin() throws Exception {
        mvc.perform(get("/chooseroom"))
                .andExpect(status().is(302));
    }
    @Test
    @WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
    @DisplayName("Status beim Starten der Webanwendung ist OK")
    void test2() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }


@Test
@WithMockOAuth2User(login="something", roles = {"USER", "ADMIN"})
@DisplayName("Get for /chooseplatz/{roomId}")
    void chooseArbeitsplatz() throws Exception {
    when(arbeitsplatzService.allArbeitsplatzeInRaumMitId(1L)).thenReturn(List.of());
        mvc.perform(get("/chooseplatz/{roomId}",1L))
                .andExpect(status().isOk())
                .andExpect(view().name("platzwählen"));
}




}