package com.huawei.ibooking.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.ibooking.model.StudentDO;
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
 * @date 2022/5/22 1:01 下午
 */

public class StudentTestUtils {
    private final static String url = "/student";

    public static StudentDO addNewStudent(MockMvc mockMvc) throws Exception {
        final StudentDO stuDo = StudentDO.builder().stuNum("test99").name("test")
                .password("test123").credit(0).email("test@qq.com").build();
        final String json = new ObjectMapper().writeValueAsString(stuDo);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        return stuDo;
    }

    public static List<StudentDO> queryAllStudent(MockMvc mockMvc) throws Exception {
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
                records.toString(), new TypeReference<List<StudentDO>>() {
                });
    }

    public static StudentDO queryStudent(StudentDO stuDo, MockMvc mockMvc) throws Exception {
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .param("stuNum", stuDo.getStuNum())
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
                records.toString(), new TypeReference<List<StudentDO>>() {
                }).get(0);
    }
}
