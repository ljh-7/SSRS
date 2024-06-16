package com.huawei.ibooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.ibooking.BookingApplication;
import com.huawei.ibooking.model.StudentDO;
import com.huawei.ibooking.utils.CommonTestUtils;
import com.huawei.ibooking.utils.StudentTestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yuqichen
 * @date 2022/5/22 9:57 上午
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BookingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebAppConfiguration
public class LoginControllerTest extends AbstractBaseTest {
    private final String url = "/login/validate";

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
    public void should_be_success_when_login_with_correct_password() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "200");
    }

    @Test
    public void should_be_fail_when_login_with_non_existing_student() throws Exception {
        final StudentDO stuDo = StudentDO.builder().stuNum("test99").name("test")
                .password("test123").credit(0).email("test@qq.com").build();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuDo))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");
    }

    @Test
    public void should_be_fail_when_login_with_wrong_password() throws Exception {
        StudentDO stuDo = StudentTestUtils.addNewStudent(mockMvc);
        StudentDO stuAdded = StudentTestUtils.queryStudent(stuDo, mockMvc);
        stuAdded.setPassword("modify123");

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(stuAdded))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String code = CommonTestUtils.getResponseCode(result.getResponse().getContentAsString());
        Assert.assertEquals(code, "400");
    }

}
