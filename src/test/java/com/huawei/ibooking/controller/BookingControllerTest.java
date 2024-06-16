package com.huawei.ibooking.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.ibooking.BookingApplication;
import com.huawei.ibooking.model.*;
import com.huawei.ibooking.utils.*;
import org.assertj.core.api.BDDAssertions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.thymeleaf.spring5.expression.Mvc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yuqichen
 * @date 2022/5/22 7:33 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BookingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebAppConfiguration
public class BookingControllerTest extends AbstractBaseTest {
    private final String url = "/ibooking";
    private final String seaturl = "/ibooking/seat";
    private final String dateurl = "/ibooking/dateSeat";

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
    public void should_be_fail_when_query_all_bookings_without_login() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        Assert.assertEquals(response.getContentAsString(), "");
    }

    @Test
    public void should_be_success_when_query_all_signed_bookings() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 17, 0, 0),
                session);
        final BookingDo bookingSign = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        List<BookingDo> expected = new ArrayList<BookingDo>();
        expected.add(bookingSign);

        BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 27, 15, 0,0),
                new Date(123, Calendar.MAY, 27, 17, 0, 0),
                session);

        final List<BookingDo> actual = BookingTestUtils.queryAllSignedBooking(mockMvc, session);
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void should_be_fail_when_query_all_signed_bookings_without_login() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("sign", "true")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        Assert.assertEquals(response.getContentAsString(), "");
    }

    @Test
    public void should_be_success_when_query_all_valid_bookings() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 17, 0, 0),
                session);
        final BookingDo bookingSign = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        List<BookingDo> expected = new ArrayList<BookingDo>();
        expected.add(bookingSign);

        BookingTestUtils.addNewNonValidBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 27, 15, 0,0),
                new Date(123, Calendar.MAY, 27, 17, 0, 0),
                session);

        final List<BookingDo> actual = BookingTestUtils.queryAllValidBooking(mockMvc, session);
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void should_be_fail_when_query_all_valid_bookings_without_login() throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("state", "true")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        Assert.assertEquals(response.getContentAsString(), "");
    }

    @Test
    public void should_be_success_when_add_a_sign_booking_with_existing_student() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 17, 0, 0),
                session);
        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);

        Assert.assertEquals(bookingDo.getSeatId(), queryDo.getSeatId());
        Assert.assertEquals(bookingDo.getStuId(), queryDo.getStuId());
        Assert.assertEquals(bookingDo.getStart(), queryDo.getStart());
        Assert.assertEquals(bookingDo.getEnd(), queryDo.getEnd());
        Assert.assertEquals(bookingDo.isSign(), queryDo.isSign());
        Assert.assertEquals(bookingDo.isState(), queryDo.isState());
    }

    @Test
    public void should_be_success_when_add_a_non_sign_booking_with_existing_student() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 17, 0, 0),
                session);
        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);

        Assert.assertEquals(bookingDo.getSeatId(), queryDo.getSeatId());
        Assert.assertEquals(bookingDo.getStuId(), queryDo.getStuId());
        Assert.assertEquals(bookingDo.getStart(), queryDo.getStart());
        Assert.assertEquals(bookingDo.getEnd(), queryDo.getEnd());
        Assert.assertEquals(bookingDo.isSign(), queryDo.isSign());
        Assert.assertEquals(bookingDo.isState(), queryDo.isState());
    }

    @Test
    public void should_be_fail_when_add_a_booking_without_login() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        BookingDo bookingDo = BookingDo.builder().stuId(stuAdded.getId()).seatId(seatAdded.getId())
                .start(new Date(123, Calendar.MAY,26, 13,0,0))
                .end(new Date(123, Calendar.MAY, 26, 16, 0, 0)).sign(false).state(true).build();

        final String json = new ObjectMapper().writeValueAsString(bookingDo);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        Assert.assertEquals(response.getContentAsString(), "");
    }

    @Test
    public void should_be_fail_when_add_a_booking_with_non_existing_student() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingDo bookingDo = BookingDo.builder().stuId(0).seatId(seatAdded.getId())
                .start(new Date(123, Calendar.MAY,26, 13,0,0))
                .end(new Date(123, Calendar.MAY, 26, 16, 0, 0)).sign(false).state(true).build();

        final String json = new ObjectMapper().writeValueAsString(bookingDo);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");
    }

    @Test
    public void should_be_fail_when_add_a_booking_with_wrong_student() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudentDO stuAno = StudentDO.builder().stuNum("test100").name("test2")
                .password("test123").credit(0).email("test@qq.com").build();

        String json = new ObjectMapper().writeValueAsString(stuAno);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        StudentDO stuAnoAdded = StudentTestUtils.queryStudent(stuAno, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingDo bookingDo = BookingDo.builder().stuId(stuAnoAdded.getId()).seatId(seatAdded.getId())
                .start(new Date(123, Calendar.MAY,26, 13,0,0))
                .end(new Date(123, Calendar.MAY, 26, 16, 0, 0)).sign(false).state(true).build();

        json = new ObjectMapper().writeValueAsString(bookingDo);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");
    }

    @Test
    public void should_be_fail_when_add_a_booking_with_non_existing_seat() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingDo bookingDo = BookingDo.builder().stuId(0).seatId(0)
                .start(new Date(123, Calendar.MAY,26, 15,0,0))
                .end(new Date(123, Calendar.MAY, 26, 17, 0, 0)).sign(false).state(true).build();

        final String json = new ObjectMapper().writeValueAsString(bookingDo);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");
    }

    @Test
    public void should_be_fail_when_add_a_booking_with_time_across_day() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingDo bookingDo = BookingDo.builder().stuId(stuAdded.getId()).seatId(seatAdded.getId())
                .start(new Date(123, Calendar.MAY,26, 15,0,0))
                .end(new Date(123, Calendar.MAY, 27, 17, 0, 0)).sign(false).state(true).build();

        final String json = new ObjectMapper().writeValueAsString(bookingDo);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");

        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        Assert.assertNull(queryDo);
    }

    @Test
    public void should_be_fail_when_add_a_booking_with_invalid_time() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingDo bookingDo = BookingDo.builder().stuId(stuAdded.getId()).seatId(seatAdded.getId())
                .start(new Date(123, Calendar.MAY,26, 4,0,0))
                .end(new Date(123, Calendar.MAY, 26, 6, 0, 0)).sign(false).state(true).build();
        System.out.println(bookingDo);
        final String json = new ObjectMapper().writeValueAsString(bookingDo);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");

        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        Assert.assertNull(queryDo);
    }

    @Test
    public void should_be_fail_when_add_a_booking_with_invalid_time_interval() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingDo bookingDo = BookingDo.builder().stuId(stuAdded.getId()).seatId(seatAdded.getId())
                .start(new Date(123, Calendar.MAY,26, 17,0,0))
                .end(new Date(123, Calendar.MAY, 26, 15, 0, 0)).sign(false).state(true).build();

        final String json = new ObjectMapper().writeValueAsString(bookingDo);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");

        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        Assert.assertNull(queryDo);
    }

    @Test
    public void should_be_fail_when_add_a_booking_with_passed_time() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingDo bookingDo = BookingDo.builder().stuId(stuAdded.getId()).seatId(seatAdded.getId())
                .start(new Date(121, Calendar.MAY,26, 15,0,0))
                .end(new Date(121, Calendar.MAY, 26, 17, 0, 0)).sign(false).state(true).build();

        final String json = new ObjectMapper().writeValueAsString(bookingDo);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");

        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        Assert.assertNull(queryDo);
    }

    @Test
    public void should_be_fail_when_add_a_booking_with_conflict_time_interval() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingDo bookingDo = BookingDo.builder().stuId(stuAdded.getId()).seatId(seatAdded.getId())
                .start(new Date(123, Calendar.MAY,26, 15,0,0))
                .end(new Date(123, Calendar.MAY, 26, 17, 0, 0)).sign(false).state(true).build();
        System.out.println(bookingDo);
        final String json = new ObjectMapper().writeValueAsString(bookingDo);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        Assert.assertNotNull(queryDo);

        BookingDo bookingDoConflict = BookingDo.builder().stuId(stuAdded.getId()).seatId(seatAdded.getId())
                .start(new Date(123, Calendar.MAY,26, 16,0,0))
                .end(new Date(123, Calendar.MAY, 26, 18, 0, 0)).sign(false).state(true).build();

        final String jsonConflict = new ObjectMapper().writeValueAsString(bookingDoConflict);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonConflict)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");

        final BookingDo queryConflict = BookingTestUtils.queryBooking(bookingDoConflict, stuDo, mockMvc, session);
        Assert.assertNull(queryConflict);
    }

    @Test
    public void should_be_fail_when_adding_the_same_booking() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 17, 0, 0),
                session);
        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);

        Assert.assertNotNull(queryDo);

        final String json = new ObjectMapper().writeValueAsString(bookingDo);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");

        result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("stu", String.valueOf(stuAdded))
                .param("seatId", String.valueOf(bookingDo.getSeatId()))
                .param("sign", String.valueOf(bookingDo.isSign()))
                .param("state", String.valueOf(bookingDo.isState()))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray records = obj.getJSONArray("data");
        List<BookingDo> bookingDos = mapper.readValue(
                records.toString(), new TypeReference<List<BookingDo>>() {
                });
        List<BookingDo> bookingDos1 = bookingDos.stream().filter(s -> s.getStart().equals(bookingDo.getStart())
                && s.getEnd().equals(bookingDo.getEnd())).collect(Collectors.toList());

        Assert.assertEquals(bookingDos1.size(), 1);
    }

    @Test
    public void should_be_success_when_signing_existing_booking() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 20, 0, 0),
                session);
        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);

        queryDo.setSign(true);

        mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(queryDo))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());
        final BookingDo queryDoNew = BookingTestUtils.queryBooking(queryDo, stuDo, mockMvc, session);

        Assert.assertEquals(queryDoNew.getSeatId(), queryDo.getSeatId());
        Assert.assertEquals(queryDoNew.getStuId(), queryDo.getStuId());
        Assert.assertEquals(queryDoNew.getStart(), queryDo.getStart());
        Assert.assertEquals(queryDoNew.getEnd(), queryDo.getEnd());
        Assert.assertEquals(queryDoNew.isSign(), queryDo.isSign());
        Assert.assertEquals(queryDoNew.isState(), queryDo.isState());
    }

    @Test
    public void should_be_fail_when_signing_non_existing_booking() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingDo bookingDo = BookingDo.builder().stuId(stuAdded.getId()).seatId(seatAdded.getId())
                .start(new Date(123, Calendar.MAY,26, 16,0,0))
                .end(new Date(123, Calendar.MAY, 26, 18, 0, 0)).sign(true).state(true).build();

        mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookingDo))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        Assert.assertNull(queryDo);
    }

    @Test
    public void should_be_fail_when_signing_existing_booking_with_wrong_student() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 20, 0, 0),
                session);
        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);

        StudentDO stuAno = StudentDO.builder().stuNum("test100").name("test2")
                .password("test123").credit(0).email("test@qq.com").build();

        String json = new ObjectMapper().writeValueAsString(stuAno);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        StudentDO stuAnoAdded = StudentTestUtils.queryStudent(stuAno, mockMvc);
        queryDo.setStuId(stuAnoAdded.getId());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(queryDo))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        String code = CommonTestUtils.getResponseCode(response.getContentAsString());
        Assert.assertEquals(code, "400");
    }

    @Test
    public void should_be_fail_when_signing_booking_without_login() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 20, 0, 0),
                session);
        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        queryDo.setSign(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(queryDo))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        Assert.assertEquals(response.getContentAsString(), "");
    }

    @Test
    public void should_be_fail_when_modifying_a_booking_with_non_existing_seat() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 20, 0, 0),
                session);
        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        queryDo.setSeatId(0);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(queryDo))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        String code = CommonTestUtils.getResponseCode(response.getContentAsString());
        Assert.assertEquals(code, "400");
    }

    @Test
    public void should_be_success_when_delete_existing_booking() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 20, 0, 0),
                session);
        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);

        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(queryDo))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo queryDel = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        Assert.assertNull(queryDel);
    }

    @Test
    public void should_be_fail_when_delete_non_existing_booking() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingDo bookingDo = BookingDo.builder().stuId(stuAdded.getId()).seatId(seatAdded.getId())
                .start(new Date(123, Calendar.MAY,26, 16,0,0))
                .end(new Date(123, Calendar.MAY, 26, 18, 0, 0)).sign(true).state(true).build();

        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookingDo))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        Assert.assertNull(queryDo);
    }

    @Test
    public void should_be_fail_when_delete_existing_booking_without_login() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        final BookingDo bookingDo = BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 20, 0, 0),
                session);
        final BookingDo queryDo = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);

        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(queryDo))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final BookingDo queryDel = BookingTestUtils.queryBooking(bookingDo, stuDo, mockMvc, session);
        Assert.assertNotNull(queryDel);
    }

    @Test
    public void should_be_success_when_get_available_seats() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        List<SeatDo> expected = new ArrayList<SeatDo>();
        expected.add(seatAdded);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(seaturl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("socket", "true")
                .param("studyRoomId", String.valueOf(studyRoomAdded.getId()))
                .param("startTime", "2023-05-26 15:00")
                .param("endTime", "2023-05-26 17:00")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray records = obj.getJSONArray("data");
        System.out.println(records);
        List<RoomSeatDo> roomSeatDos = mapper.readValue(
                records.toString(), new TypeReference<List<RoomSeatDo>>() {
                });

        List<SeatDo> actual = new ArrayList<SeatDo>();
        for (RoomSeatDo roomSeatDo: roomSeatDos) {
            actual.add(SeatDo.builder().id(roomSeatDo.getId()).num(roomSeatDo.getNum())
                    .socket(roomSeatDo.isSocket()).studyRoomId(roomSeatDo.getStudyRoomId()).build());
        }
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void should_be_fail_when_get_available_seats_with_invalid_start_time() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(seaturl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("socket", "true")
                .param("studyRoomId", String.valueOf(studyRoomAdded.getId()))
                .param("startTime", "2023-05-26 04:00")
                .param("endTime", "2023-05-26 05:59")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray records = obj.getJSONArray("data");
        System.out.println(records);
        List<RoomSeatDo> roomSeatDos = mapper.readValue(
                records.toString(), new TypeReference<List<RoomSeatDo>>() {
                });
        Assert.assertEquals(roomSeatDos.size(), 0);
    }

    @Test
    public void should_be_fail_when_get_available_seats_with_invalid_time_interval() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(seaturl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("socket", "true")
                .param("studyRoomId", String.valueOf(studyRoomAdded.getId()))
                .param("startTime", "2023-05-26 17:00")
                .param("endTime", "2023-05-26 15:00")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        String code = CommonTestUtils.getResponseCode(response.getContentAsString());
        Assert.assertEquals(code, "400");
    }

    @Test
    public void should_be_success_when_get_available_seats_with_time_conflict_consideration() throws Exception {
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

        BookingTestUtils.addNewSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 20, 0, 0),
                session);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(seaturl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("socket", "true")
                .param("studyRoomId", String.valueOf(studyRoomAdded.getId()))
                .param("startTime", "2023-05-26 16:00")
                .param("endTime", "2023-05-26 18:00")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray records = obj.getJSONArray("data");
        List<RoomSeatDo> roomSeatDos = mapper.readValue(
                records.toString(), new TypeReference<List<RoomSeatDo>>() {
                });
        Assert.assertEquals(roomSeatDos.size(), 0);
    }

    @Test
    public void should_be_success_when_get_available_seats_with_time_conflict_consideration_ano() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 15, 0,0),
                new Date(123, Calendar.MAY, 26, 17, 0, 0),
                session);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(seaturl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("socket", "true")
                .param("studyRoomId", String.valueOf(studyRoomAdded.getId()))
                .param("startTime", "2023-05-26 14:00")
                .param("endTime", "2023-05-26 18:00")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray records = obj.getJSONArray("data");
        List<RoomSeatDo> roomSeatDos = mapper.readValue(
                records.toString(), new TypeReference<List<RoomSeatDo>>() {
                });
        System.out.println(roomSeatDos.toString());
        Assert.assertEquals(roomSeatDos.size(), 0);
    }

    @Test
    public void should_be_success_when_get_available_seats_with_multiple_time_conflicts() throws Exception {
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(seaturl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("socket", "true")
                .param("studyRoomId", String.valueOf(studyRoomAdded.getId()))
                .param("startTime", "2023-05-26 12:00")
                .param("endTime", "2023-05-26 16:00")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray records = obj.getJSONArray("data");
        List<RoomSeatDo> roomSeatDos = mapper.readValue(
                records.toString(), new TypeReference<List<RoomSeatDo>>() {
                });

        Assert.assertEquals(roomSeatDos.size(), 0);
    }

    @Test
    public void should_be_success_when_get_available_seats_with_no_time_conflicts() throws Exception {
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
                new Date(123, Calendar.MAY, 26, 16, 0,0),
                new Date(123, Calendar.MAY, 26, 20, 0, 0),
                session);

        BookingTestUtils.addNewNonSignBooking(mockMvc, stuAdded.getId(), seatAdded.getId(),
                new Date(123, Calendar.MAY, 26, 10, 0,0),
                new Date(123, Calendar.MAY, 26, 12, 0, 0),
                session);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(seaturl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("socket", "true")
                .param("studyRoomId", String.valueOf(studyRoomAdded.getId()))
                .param("startTime", "2023-05-26 13:00")
                .param("endTime", "2023-05-26 14:00")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray records = obj.getJSONArray("data");
        List<RoomSeatDo> roomSeatDos = mapper.readValue(
                records.toString(), new TypeReference<List<RoomSeatDo>>() {
                });

        Assert.assertEquals(roomSeatDos.size(), 1);
    }

    @Test
    public void should_be_success_when_query_available_time_for_existing_seat_with_no_booking() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(dateurl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("seatId", String.valueOf(seatAdded.getId()))
                .param("startTime", "2023-05-26")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        Assert.assertEquals(obj.toString(), "{\"msg\":\"success\",\"code\":\"200\",\"data\":[[6,23]]}");
    }

    @Test
    public void should_be_success_when_query_available_time_for_existing_seat_with_one_booking() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);

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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(dateurl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("seatId", String.valueOf(seatAdded.getId()))
                .param("startTime", "2023-05-26")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        Assert.assertEquals(obj.toString(), "{\"msg\":\"success\",\"code\":\"200\",\"data\":[[6,15],[20,23]]}");
    }

    @Test
    public void should_be_success_when_query_available_time_for_existing_seat_with_multiple_bookings() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDO, mockMvc);

        SeatDo seatDo = SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());
        SeatDo seatAdded = SeatTestUtils.querySeat(seatDo, mockMvc);
        System.out.println(seatAdded);

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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(dateurl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("seatId", String.valueOf(seatAdded.getId()))
                .param("startTime", "2023-05-26")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        Assert.assertEquals(obj.toString(), "{\"msg\":\"success\",\"code\":\"200\",\"data\":[[6,10],[13,15],[20,23]]}");
    }

    @Test
    public void should_be_fail_when_query_available_time_for_non_existing_seat() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/login/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(dateurl)
                .contentType(MediaType.APPLICATION_JSON)
                .param("seatId", String.valueOf(0))
                .param("startTime", "2023-05-26")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);

        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("utf-8");
        String code = CommonTestUtils.getResponseCode(response.getContentAsString());
        Assert.assertEquals(code, "400");
    }
}
