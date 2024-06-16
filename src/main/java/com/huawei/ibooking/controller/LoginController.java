package com.huawei.ibooking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huawei.ibooking.business.StudentBusiness;
import com.huawei.ibooking.model.MyResponseBody;
import com.huawei.ibooking.model.StudentDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class LoginController {
    @Autowired
    private StudentBusiness studentBusiness;

    /**
     *
     * @param stu 前端传过来的参数必须要有stuNum和password属性
     * @param session
     * @return
     */
    @PostMapping(value = "/login/validate")
    public MyResponseBody query(@RequestBody StudentDO stu, HttpSession session) {
        QueryWrapper<StudentDO> studentDOQueryWrapper = new QueryWrapper<>();
        studentDOQueryWrapper.eq("stuNum", stu.getStuNum());
        StudentDO res = studentBusiness.getOne(studentDOQueryWrapper);
        if (res == null)
            return new MyResponseBody("400", "用户不存在", null);
        else if(!res.getPassword().equals(stu.getPassword()))
            return new MyResponseBody("400", "密码不正确", null);
        else {
            session.setAttribute("user", res);
            StudentDO studentDO = new StudentDO();
            studentDO.setName(res.getName());
            studentDO.setId(res.getId());
            return new MyResponseBody("200", "登录成功", studentDO);
        }
    }
}
