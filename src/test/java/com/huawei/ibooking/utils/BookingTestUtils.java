package com.huawei.ibooking.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.ibooking.model.BookingDo;
import com.huawei.ibooking.model.SeatDo;
import com.huawei.ibooking.model.StudentDO;
import com.huawei.ibooking.model.StudyRoomDO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yuqichen
 * @date 2022/5/22 8:05 下午
 */
public class BookingTestUtils {
    private final static String url = "/ibooking";

    public static BookingDo addNewSignBooking(MockMvc mockMvc, Integer stuId, Integer seatId, Date start, Date end, MockHttpSession session) throws Exception {
        final BookingDo bookingDo = BookingDo.builder().stuId(stuId).seatId(seatId).start(start).end(end).sign(true).state(true).build();
        final String json = new ObjectMapper().writeValueAsString(bookingDo);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        return bookingDo;
    }

    public static BookingDo addNewNonSignBooking(MockMvc mockMvc, Integer stuId, Integer seatId, Date start, Date end, MockHttpSession session) throws Exception {
        final BookingDo bookingDo = BookingDo.builder().stuId(stuId).seatId(seatId).start(start).end(end).sign(false).state(true).build();
        final String json = new ObjectMapper().writeValueAsString(bookingDo);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        return bookingDo;
    }

    public static BookingDo addNewNonValidBooking(MockMvc mockMvc, Integer stuId, Integer seatId, Date start, Date end, MockHttpSession session) throws Exception {
        final BookingDo bookingDo = BookingDo.builder().stuId(stuId).seatId(seatId).start(start).end(end).sign(false).state(false).build();
        final String json = new ObjectMapper().writeValueAsString(bookingDo);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk());

        return bookingDo;
    }

    public static List<BookingDo> queryAllValidBooking(MockMvc mockMvc, MockHttpSession session) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("state", "true")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray records = obj.getJSONArray("data");
        return mapper.readValue(
                records.toString(), new TypeReference<List<BookingDo>>() {
                });
    }

    public static List<BookingDo> queryAllSignedBooking(MockMvc mockMvc, MockHttpSession session) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("sign", "true")
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray records = obj.getJSONArray("data");
        return mapper.readValue(
                records.toString(), new TypeReference<List<BookingDo>>() {
                });
    }

    public static List<BookingDo> queryAllBooking(MockMvc mockMvc, MockHttpSession session) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray records = obj.getJSONArray("data");
        return mapper.readValue(
                records.toString(), new TypeReference<List<BookingDo>>() {
                });
    }

    public static BookingDo queryBooking(BookingDo bookingDo, StudentDO studentDO, MockMvc mockMvc, MockHttpSession session) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("stu", String.valueOf(studentDO))
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
        if (bookingDos1.size() == 0) {
            return null;
        } else {
            return bookingDos.get(0);
        }
    }
}
