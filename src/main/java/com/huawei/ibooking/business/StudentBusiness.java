package com.huawei.ibooking.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huawei.ibooking.mapper.StudentMapper;
import com.huawei.ibooking.model.StudentDO;
import com.huawei.ibooking.service.StudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentBusiness extends ServiceImpl<StudentMapper, StudentDO> implements StudentService {

    public IPage<StudentDO> getStudents(String stuNum, String name, Integer credit,
                                        Integer pageNo, Integer size) {
        QueryWrapper<StudentDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(stuNum), "stuNum", stuNum)
                .eq(StringUtils.isNotBlank(name), "name", name)
                .eq(credit != null, "credit", credit);
        Page<StudentDO> page = new Page<>(pageNo == null? 1 : pageNo,
                size == null ? this.count(queryWrapper) : size);
        return this.page(page, queryWrapper);
    }

    /**
     * 验证学生密码是否正确
     *
     * @param stu
     * @return
     */
    public StudentDO validateStudent(StudentDO stu) {
        QueryWrapper<StudentDO> studentDOQueryWrapper = new QueryWrapper<>();
        studentDOQueryWrapper.eq("stuNum", stu.getStuNum());
        StudentDO res = this.getOne(studentDOQueryWrapper);
        if (res != null && res.getPassword().equals(stu.getPassword()))
            return res;
        return null;
    }
}
