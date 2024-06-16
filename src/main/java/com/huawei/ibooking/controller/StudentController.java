package com.huawei.ibooking.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huawei.ibooking.business.StudentBusiness;
import com.huawei.ibooking.validator.UpdateAction;
import com.huawei.ibooking.model.MyResponseBody;
import com.huawei.ibooking.model.StudentDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;

@RestController
@Validated
public class StudentController {
    @Autowired
    private StudentBusiness stuBiz;

    /**
     * 根据学号，姓名，失信次数筛选学生，参数可为null
     *
     * @param stuNum
     * @param name
     * @param credit
     * @return
     */
    @GetMapping(value = "/student")
    public MyResponseBody list(@Param("stuNum") String stuNum,
                               @Param("name") String name,
                               @Param("credit") Integer credit,
                               @Param("page") Integer page,
                               @Param("size") Integer rows) {
        IPage<StudentDO> students = stuBiz.getStudents(stuNum, name, credit, page, rows);
        return new MyResponseBody("200", "success", students);
    }

    @PostMapping(value = "/student")
    public MyResponseBody add(@Validated @RequestBody StudentDO student) {
        boolean result = stuBiz.save(student);
        return new MyResponseBody(result ? "200" : "400", result ? "success" : "failed", result);
    }

    @PutMapping(value = "/student")
    public MyResponseBody save(@RequestBody @Validated({UpdateAction.class, Default.class}) StudentDO student) {
        boolean result = stuBiz.updateById(student);
        return new MyResponseBody(result ? "200" : "400", result ? "success" : "failed", result);
    }

    @DeleteMapping(value = "/student")
    public MyResponseBody delete(@RequestBody @Validated({UpdateAction.class, Default.class}) StudentDO student) {
        boolean result = stuBiz.removeById(student.getId());
        return new MyResponseBody(result ? "200" : "400", result ? "success" : "failed", result);
    }
}
