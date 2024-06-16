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
import org.thymeleaf.spring5.expression.Mvc;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yuqichen
 * @date 2022/5/22 10:21 上午
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BookingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebAppConfiguration
public class StudyRoomControllerTest extends AbstractBaseTest {
    private final String url = "/studyroom";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void should_be_success_when_query_all_studyroom() throws Exception {
        StudyRoomDO room1 = StudyRoomDO.builder().id(1).buildingNum("Building No.1").classRoomNum("1001")
                .startTime(7).endTime(22).build();
        StudyRoomDO room2 = StudyRoomDO.builder().id(2).buildingNum("Building No.1").classRoomNum("1002")
                .startTime(7).endTime(22).build();
        StudyRoomDO room3 = StudyRoomDO.builder().id(3).buildingNum("Building No.2").classRoomNum("2001")
                .startTime(7).endTime(22).build();
        StudyRoomDO room4 = StudyRoomDO.builder().id(4).buildingNum("Building No.2").classRoomNum("2002")
                .startTime(7).endTime(22).build();
        List<StudyRoomDO> expected = new ArrayList<StudyRoomDO>();
        expected.add(room1);
        expected.add(room2);
        expected.add(room3);
        expected.add(room4);

        final List<StudyRoomDO> acutal = StudyRoomTestUtils.queryAllStudyRoom(mockMvc);
        // Assert.assertEquals(expected.toString(), acutal.toString());
    }

    @Test
    public void should_be_success_when_add_a_new_studyroom() throws Exception {
        final StudyRoomDO studyRoomDo = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        final StudyRoomDO queryDo = StudyRoomTestUtils.queryStudyRoom(studyRoomDo, mockMvc);

        Assert.assertEquals(studyRoomDo.getBuildingNum(), queryDo.getBuildingNum());
        Assert.assertEquals(studyRoomDo.getClassRoomNum(), queryDo.getClassRoomNum());
        Assert.assertEquals(studyRoomDo.getStartTime(), queryDo.getStartTime());
        Assert.assertEquals(studyRoomDo.getEndTime(), queryDo.getEndTime());
    }

    @Test
    public void should_be_fail_when_add_a_new_studyroom_with_invalid_open_time() throws Exception {
        final StudyRoomDO studyRoomDO = StudyRoomDO.builder().buildingNum("Testing Building").classRoomNum("1001")
                .startTime(23).endTime(6).build();
        final String json = new ObjectMapper().writeValueAsString(studyRoomDO);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }

    @Test
    public void should_be_fail_when_add_a_new_studyroom_with_start_time_less_than_zero() throws Exception {
        final StudyRoomDO studyRoomDO = StudyRoomDO.builder().buildingNum("Testing Building").classRoomNum("1001")
                .startTime(-3).endTime(22).build();
        final String json = new ObjectMapper().writeValueAsString(studyRoomDO);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }

    @Test
    public void should_be_fail_when_add_a_new_studyroom_with_end_time_great_than_twenty_four() throws Exception {
        final StudyRoomDO studyRoomDO = StudyRoomDO.builder().buildingNum("Testing Building").classRoomNum("1001")
                .startTime(6).endTime(26).build();
        final String json = new ObjectMapper().writeValueAsString(studyRoomDO);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }

    @Test
    public void should_be_fail_when_modifying_non_existing_studyroom() throws Exception {
        final StudyRoomDO studyRoomDO = StudyRoomDO.builder().buildingNum("Testing Building").classRoomNum("1001")
                .startTime(6).endTime(23).build();
        studyRoomDO.setStartTime(7);
        final String json = new ObjectMapper().writeValueAsString(studyRoomDO);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }

    @Test
    public void should_be_fail_when_adding_the_same_studyroom() throws Exception {
        final StudyRoomDO studyRoomDO = StudyRoomTestUtils.addNewStudyRoom(mockMvc);

        final String json = new ObjectMapper().writeValueAsString(studyRoomDO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk()).andReturn();
        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");

        List<StudyRoomDO> studyRoomDOS = StudyRoomTestUtils.queryAllStudyRoom(mockMvc);
        List<StudyRoomDO> filterStudyRoom = studyRoomDOS.stream().filter(s -> s.getClassRoomNum().
                equals(studyRoomDO.getClassRoomNum()) && s.getBuildingNum().equals(studyRoomDO.getBuildingNum()))
                .collect(Collectors.toList());
        Assert.assertEquals(filterStudyRoom.size(), 1);
    }

    @Test
    public void should_be_success_when_modifying_existing_studyroom() throws Exception {
        final StudyRoomDO studyRoomDo = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        final StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDo, mockMvc);
        studyRoomAdded.setStartTime(8);

        mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(studyRoomAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final StudyRoomDO queryDo = StudyRoomTestUtils.queryStudyRoom(studyRoomAdded, mockMvc);

        Assert.assertEquals(studyRoomAdded.getBuildingNum(), queryDo.getBuildingNum());
        Assert.assertEquals(studyRoomAdded.getClassRoomNum(), queryDo.getClassRoomNum());
        Assert.assertEquals(studyRoomAdded.getStartTime(), queryDo.getStartTime());
        Assert.assertEquals(studyRoomAdded.getEndTime(), queryDo.getEndTime());
    }

    @Test
    public void should_be_fail_when_modifying_existing_studyroom_with_invalid_open_time() throws Exception {
        final StudyRoomDO studyRoomDo = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        final StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDo, mockMvc);
        studyRoomAdded.setStartTime(23);
        studyRoomAdded.setEndTime(6);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(studyRoomAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }

    @Test
    public void should_be_fail_when_modifying_existing_studyroom_with_start_time_less_than_zero() throws Exception {
        final StudyRoomDO studyRoomDo = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        final StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDo, mockMvc);
        studyRoomAdded.setStartTime(-3);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(studyRoomAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }

    @Test
    public void should_be_fail_when_modifying_existing_studyroom_with_end_time_great_than_twenty_four() throws Exception {
        final StudyRoomDO studyRoomDo = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        final StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDo, mockMvc);
        studyRoomAdded.setEndTime(26);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(studyRoomAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }

    @Test
    public void should_be_success_when_delete_existing_studyroom() throws Exception {
        final StudyRoomDO studyRoomDo = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        final StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDo, mockMvc);

        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(studyRoomAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<StudyRoomDO> studyRoomDOS = StudyRoomTestUtils.queryAllStudyRoom(mockMvc);
        System.out.println(studyRoomDOS);
        List<StudyRoomDO> studyRoomDO = studyRoomDOS.stream().filter(s -> s.getBuildingNum().equals(studyRoomAdded.getBuildingNum())
                && s.getClassRoomNum().equals(studyRoomAdded.getClassRoomNum()) ).collect(Collectors.toList());
        Assert.assertEquals(studyRoomDO.size(), 0);
    }

    @Test
    public void should_be_fail_when_delete_non_existing_studyroom() throws Exception {
        final StudyRoomDO studyRoomDO = StudyRoomDO.builder().buildingNum("Testing Building").classRoomNum("1001")
                .startTime(6).endTime(23).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(studyRoomDO))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "500");
    }

    @Test
    public void should_be_success_when_delete_existing_studyroom_with_seats() throws Exception {
        final StudyRoomDO studyRoomDo = StudyRoomTestUtils.addNewStudyRoom(mockMvc);
        final StudyRoomDO studyRoomAdded = StudyRoomTestUtils.queryStudyRoom(studyRoomDo, mockMvc);

        SeatTestUtils.addNewSocketSeat(mockMvc, studyRoomAdded.getId());

        List<SeatDo> seatDos = SeatTestUtils.queryAllSeat(mockMvc);
        List<SeatDo> filteredSeats = seatDos.stream().filter(s -> s.getStudyRoomId().equals(studyRoomAdded.getId()))
                .collect(Collectors.toList());
        Assert.assertEquals(filteredSeats.size(), 1);

        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(studyRoomAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<StudyRoomDO> studyRoomDOS = StudyRoomTestUtils.queryAllStudyRoom(mockMvc);
        List<StudyRoomDO> filteredStudyRooms = studyRoomDOS.stream().filter(s -> s.getBuildingNum().equals(studyRoomAdded.getBuildingNum())
                && s.getClassRoomNum().equals(studyRoomAdded.getClassRoomNum()) ).collect(Collectors.toList());
        Assert.assertEquals(filteredStudyRooms.size(), 0);

        seatDos = SeatTestUtils.queryAllSeat(mockMvc);
        filteredSeats = seatDos.stream().filter(s -> s.getStudyRoomId().equals(studyRoomAdded.getId()))
                .collect(Collectors.toList());
        Assert.assertEquals(filteredSeats.size(), 0);
    }
}
