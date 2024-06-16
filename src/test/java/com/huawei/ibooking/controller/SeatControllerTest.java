package com.huawei.ibooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.ibooking.BookingApplication;
import com.huawei.ibooking.model.BookingDo;
import com.huawei.ibooking.model.SeatDo;
import com.huawei.ibooking.model.StudentDO;
import com.huawei.ibooking.model.StudyRoomDO;
import com.huawei.ibooking.utils.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yuqichen
 * @date 2022/5/22 12:42 下午
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BookingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebAppConfiguration
public class SeatControllerTest extends AbstractBaseTest {
    private final String url = "/seat";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private MockHttpSession session;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        session = new MockHttpSession();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void should_be_success_when_query_all_seats() throws Exception {
        SeatDo seat1 = SeatDo.builder().id(1).studyRoomId(1).num(2).socket(true).build();
        SeatDo seat2 = SeatDo.builder().id(2).studyRoomId(2).num(3).socket(false).build();
        SeatDo seat3 = SeatDo.builder().id(3).studyRoomId(1).num(4).socket(false).build();
        SeatDo seat4 = SeatDo.builder().id(4).studyRoomId(2).num(5).socket(true).build();
        List<SeatDo> expected = new ArrayList<SeatDo>();
        expected.add(seat1);
        expected.add(seat2);
        expected.add(seat3);
        expected.add(seat4);

        final List<SeatDo> actual = SeatTestUtils.queryAllSeat(mockMvc);
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void should_be_success_when_query_all_socket_seats() throws Exception {
        SeatDo seat1 = SeatDo.builder().id(1).studyRoomId(1).num(2).socket(true).build();
        SeatDo seat4 = SeatDo.builder().id(4).studyRoomId(2).num(5).socket(true).build();
        List<SeatDo> expected = new ArrayList<SeatDo>();
        expected.add(seat1);
        expected.add(seat4);

        final List<SeatDo> actual = SeatTestUtils.queryAllSocketSeat(mockMvc);
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void should_be_success_when_query_all_non_socket_seats() throws Exception {
        SeatDo seat2 = SeatDo.builder().id(2).studyRoomId(2).num(3).socket(false).build();
        SeatDo seat3 = SeatDo.builder().id(3).studyRoomId(1).num(4).socket(false).build();
        List<SeatDo> expected = new ArrayList<SeatDo>();
        expected.add(seat2);
        expected.add(seat3);

        final List<SeatDo> actual = SeatTestUtils.queryAllNonSocketSeat(mockMvc);
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void should_be_success_when_add_a_new_socket_seat_in_existing_studyroom() throws Exception {
        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo queryDo = SeatTestUtils.querySeat(seatDo, mockMvc);

        Assert.assertEquals(seatDo.getStudyRoomId(), queryDo.getStudyRoomId());
        Assert.assertEquals(seatDo.getNum(), queryDo.getNum());
    }

    @Test
    public void should_be_success_when_add_a_new_non_socket_seat_in_existing_studyroom() throws Exception {
        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewNonSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo queryDo = SeatTestUtils.querySeat(seatDo, mockMvc);

        Assert.assertEquals(seatDo.getStudyRoomId(), queryDo.getStudyRoomId());
        Assert.assertEquals(seatDo.getNum(), queryDo.getNum());
    }

    @Test
    public void should_be_fail_when_add_a_new_seat_in_non_existing_studyroom() throws Exception {
        final SeatDo seatDo = SeatDo.builder().studyRoomId(0).num(1).socket(true).build();
        final String json = new ObjectMapper().writeValueAsString(seatDo);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");

        List<SeatDo> seatDos = SeatTestUtils.queryAllSeat(mockMvc);
        List<SeatDo> filteredSeats = seatDos.stream().filter(s -> s.getStudyRoomId().equals(0))
                .collect(Collectors.toList());
        Assert.assertEquals(filteredSeats.size(), 0);
    }

    @Test
    public void should_be_fail_when_add_the_same_seat_in_the_same_studyroom() throws Exception {
        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewNonSocketSeat(mockMvc, studyRoomAdded.getId());
        final String json = new ObjectMapper().writeValueAsString(seatDo);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");

        List<SeatDo> seatDos = SeatTestUtils.queryAllSeat(mockMvc);
        List<SeatDo> filterSeat = seatDos.stream().filter(s -> s.getStudyRoomId().
                equals(seatDo.getStudyRoomId()) && s.getNum().equals(seatDo.getNum())).collect(Collectors.toList());
        Assert.assertEquals(filterSeat.size(), 1);
    }

    @Test
    public void should_be_success_when_modifying_existing_seat() throws Exception {
        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        final SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        final SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);
        seatAdded.setSocket(false);

        mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(seatAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final SeatDo queryDo = SeatTestUtils.querySeat(seatDo, mockMvc);

        Assert.assertEquals(seatAdded.getNum(), queryDo.getNum());
        Assert.assertEquals(seatAdded.getStudyRoomId(), queryDo.getStudyRoomId());
        Assert.assertEquals(seatAdded.isSocket(), queryDo.isSocket());
    }

    @Test
    public void should_be_fail_when_modifying_non_existing_seat() throws Exception {
        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        final SeatDo seatDo = SeatDo.builder().studyRoomId(studyRoomAdded.getId()).num(1).socket(true).build();
        seatDo.setSocket(false);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(seatDo))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }

    @Test
    public void should_be_success_when_delete_existing_seat() throws Exception {
        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        final SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        final SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(seatAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<SeatDo> seatDos = SeatTestUtils.queryAllSeat(mockMvc);
        List<SeatDo> seatDO = seatDos.stream().filter(s -> s.getStudyRoomId().equals(seatAdded.getStudyRoomId())
                && s.getNum().equals(seatAdded.getNum())).collect(Collectors.toList());
        Assert.assertEquals(seatDO.size(), 0);
    }

    @Test
    public void should_be_success_when_delete_existing_seat_with_bookings() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        System.out.println(seatAdded.toString());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 20, 0, 0),
                session);

        BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 10, 0,0),
                new Date(123, Calendar.MAY, 26, 13, 0, 0),
                session);

        List<BookingDo> bookingDos = BookingTestUtils.queryAllBooking(mockMvc, session);
        // Assert.assertEquals(bookingDos.size(), 2);

        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(seatAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<SeatDo> seatDos = SeatTestUtils.queryAllSeat(mockMvc);
        List<SeatDo> seatDO = seatDos.stream().filter(s -> s.getStudyRoomId().equals(seatAdded.getStudyRoomId())
                && s.getNum().equals(seatAdded.getNum())).collect(Collectors.toList());
        Assert.assertEquals(seatDO.size(), 0);

        List<BookingDo> bookingDos2 = BookingTestUtils.queryAllBooking(mockMvc, session);
        Assert.assertEquals(bookingDos2.size(), 0);
    }

    @Test
    public void should_be_fail_when_delete_non_existing_seat() throws Exception {
        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        final SeatDo seatDo = SeatDo.builder().studyRoomId(studyRoomAdded.getId()).num(1).socket(true).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(seatDo))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }
}
