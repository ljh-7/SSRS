package com.huawei.ibooking.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.ibooking.model.SeatDo;
import com.huawei.ibooking.model.StudyRoomDO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yuqichen
 * @date 2022/5/22 1:50 下午
 */
public class SeatTestUtils {
    private final static String url = "/seat";

    public static SeatDo addNewSocketSeat(MockMvc mockMvc, Integer studyRoomId) throws Exception {
        final SeatDo seatDo = SeatDo.builder().studyRoomId(studyRoomId).num(1).socket(true).build();
        final String json = new ObjectMapper().writeValueAsString(seatDo);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        return seatDo;
    }

    public static SeatDo addNewNonSocketSeat(MockMvc mockMvc, Integer studyRoomId) throws Exception {
        final SeatDo seatDo = SeatDo.builder().studyRoomId(studyRoomId).num(2).socket(false).build();
        final String json = new ObjectMapper().writeValueAsString(seatDo);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        return seatDo;
    }

    public static List<SeatDo> queryAllSocketSeat(MockMvc mockMvc) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("socket", "true")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONObject responseData = obj.getJSONObject("data");
        JSONArray records = responseData.getJSONArray("records");
        return mapper.readValue(
                records.toString(), new TypeReference<List<SeatDo>>() {
                });
    }

    public static List<SeatDo> queryAllNonSocketSeat(MockMvc mockMvc) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("socket", "false")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONObject responseData = obj.getJSONObject("data");
        JSONArray records = responseData.getJSONArray("records");
        return mapper.readValue(
                records.toString(), new TypeReference<List<SeatDo>>() {
                });
    }

    public static List<SeatDo> queryAllSeat(MockMvc mockMvc) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONObject responseData = obj.getJSONObject("data");
        JSONArray records = responseData.getJSONArray("records");
        return mapper.readValue(
                records.toString(), new TypeReference<List<SeatDo>>() {
                });
    }

    public static List<SeatDo> querySeatWithStudyRoom(StudyRoomDO studyRoomDO, MockMvc mockMvc) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("studyRoomId", String.valueOf(studyRoomDO.getId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONObject responseData = obj.getJSONObject("data");
        JSONArray records = responseData.getJSONArray("records");
        return mapper.readValue(
                records.toString(), new TypeReference<List<SeatDo>>() {
                });
    }

    public static SeatDo querySeat(SeatDo seatDo, MockMvc mockMvc) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("studyRoomId", String.valueOf(seatDo.getStudyRoomId()))
                .param("num", String.valueOf(seatDo.getNum()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        ObjectMapper mapper = new ObjectMapper(factory);

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        JSONObject responseData = obj.getJSONObject("data");
        JSONArray records = responseData.getJSONArray("records");
        return mapper.readValue(
                records.toString(), new TypeReference<List<SeatDo>>() {
                }).get(0);
    }
}
