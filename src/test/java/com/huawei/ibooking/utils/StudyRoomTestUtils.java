package com.huawei.ibooking.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.ibooking.model.StudyRoomDO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yuqichen
 * @date 2022/5/22 1:07 下午
 */
public class StudyRoomTestUtils {
    private static final String url = "/studyroom";

    public static StudyRoomDO addNewStudyRoom(MockMvc mockMvc) throws Exception {
        final StudyRoomDO studyRoomDO = StudyRoomDO.builder().buildingNum("Testing Building").classRoomNum("1001")
                .startTime(6).endTime(23).build();
        final String json = new ObjectMapper().writeValueAsString(studyRoomDO);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        return studyRoomDO;
    }

    public static List<StudyRoomDO> queryAllStudyRoom(MockMvc mockMvc) throws Exception {
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
                records.toString(), new TypeReference<List<StudyRoomDO>>() {
                });
    }

    public static StudyRoomDO queryStudyRoom(StudyRoomDO studyRoomDO, MockMvc mockMvc) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("buildingNum", studyRoomDO.getBuildingNum())
                .param("classRoomNum", studyRoomDO.getClassRoomNum())
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
                records.toString(), new TypeReference<List<StudyRoomDO>>() {
                }).get(0);
    }
}
