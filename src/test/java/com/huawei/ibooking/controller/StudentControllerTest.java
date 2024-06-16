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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BookingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebAppConfiguration
@AutoConfigureMockMvc
public class StudentControllerTest extends AbstractBaseTest {
    private final String url = "/student";

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
    public void should_be_success_when_query_all_students() throws Exception {
        StudentDO student1 = StudentDO.builder().id(1).stuNum("21210240333").name("Wang FuLong")
                .password("123456").credit(0).email("1091876908@qq.com").build();
        StudentDO student2 = StudentDO.builder().id(2).stuNum("21210240337").name("Wang MingLong")
                .password("123456").credit(0).email("2212688995@qq.com").build();
        StudentDO student3 = StudentDO.builder().id(3).stuNum("21210240334").name("Wang HaoJin")
                .password("123456").credit(0).email("1312220946@qq.com").build();
        StudentDO student4 = StudentDO.builder().id(4).stuNum("21210240134").name("Chen YuQi")
                .password("123456").credit(0).email("1378796625@qq.com").build();
        List<StudentDO> expected = new ArrayList<StudentDO>();
        expected.add(student1);
        expected.add(student2);
        expected.add(student3);
        expected.add(student4);

        final List<StudentDO> actual = StudentTestUtils.queryAllStudent(mockMvc);
        Assert.assertEquals(actual.toString(), expected.toString());
    }

    @Test
    public void should_be_success_when_add_a_new_student() throws Exception {
        final StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        final StudentDO queryDo = StudentTestUtils.queryStudent(stuDo, mockMvc);

        Assert.assertEquals(stuDo.getStuNum(), queryDo.getStuNum());
        Assert.assertEquals(stuDo.getName(), queryDo.getName());
        Assert.assertEquals(stuDo.getPassword(), queryDo.getPassword());
        Assert.assertEquals(stuDo.getCredit(), queryDo.getCredit());
        Assert.assertEquals(stuDo.getEmail(), queryDo.getEmail());
    }

    @Test
    public void should_be_fail_when_adding_the_same_student() throws Exception {
        final StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);

        final String json = new ObjectMapper().writeValueAsString(stuDo);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");

        List<StudentDO> studentDOS = StudentTestUtils.queryAllStudent(mockMvc);
        List<StudentDO> filterStudent = studentDOS.stream().filter(s -> s.getStuNum().
                equals(stuDo.getStuNum())).collect(Collectors.toList());
        Assert.assertEquals(filterStudent.size(), 1);
    }

    @Test
    public void should_be_success_when_modifying_existing_student() throws Exception {
        final StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        final StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);
        stuAdded.setPassword("modify123");

        mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final StudentDO queryDo = StudentTestUtils.queryStudent(stuDo, mockMvc);

        Assert.assertEquals(stuAdded.getStuNum(), queryDo.getStuNum());
        Assert.assertEquals(stuAdded.getName(), queryDo.getName());
        Assert.assertEquals(stuAdded.getCredit(), queryDo.getCredit());
        Assert.assertEquals(stuAdded.getPassword(), queryDo.getPassword());
        Assert.assertEquals(stuAdded.getEmail(), queryDo.getEmail());
    }

    @Test
    public void should_be_fail_when_modifying_non_existing_student()  throws Exception{
        final StudentDO stuDo = StudentDO.builder().stuNum("test99").name("test")
                .password("test123").credit(0).email("test@qq.com").build();
        stuDo.setPassword("modify123");
        final String json = new ObjectMapper().writeValueAsString(stuDo);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }

    @Test
    public void should_be_success_when_delete_existing_student() throws Exception {
        final StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        final StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<StudentDO> studentDOS = StudentTestUtils.queryAllStudent(mockMvc);
        List<StudentDO> studentDO = studentDOS.stream().filter(s -> s.getStuNum().equals(stuDo.getStuNum())).collect(Collectors.toList());
        Assert.assertEquals(studentDO.size(), 0);
    }

    @Test
    public void should_be_success_when_delete_existing_student_with_bookings() throws Exception {
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
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<StudentDO> studentDOS = StudentTestUtils.queryAllStudent(mockMvc);
        List<StudentDO> studentDO = studentDOS.stream().filter(s -> s.getStuNum().equals(stuDo.getStuNum())).collect(Collectors.toList());
        Assert.assertEquals(studentDO.size(), 0);

        List<BookingDo> bookingDos2 = BookingTestUtils.queryAllBooking(mockMvc, session);
        Assert.assertEquals(bookingDos2.size(), 0);
    }

    @Test
    public void should_be_fail_when_delete_non_existing_student() throws Exception {
        final StudentDO stuDo = StudentDO.builder().stuNum("test99").name("test")
                .password("test123").credit(0).email("test@qq.com").build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuDo))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }
}