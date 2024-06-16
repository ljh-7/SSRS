package com.huawei.ibooking.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huawei.ibooking.business.StudyRoomBusiness;
import com.huawei.ibooking.validator.UpdateAction;
import com.huawei.ibooking.model.MyResponseBody;
import com.huawei.ibooking.model.StudyRoomDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;

@RestController
public class StudyRoomController {
    @Autowired
    private StudyRoomBusiness studyRoomBusiness;

    @GetMapping(value = "/studyroom")
    public MyResponseBody list(@Param("buildingNum") String buildingNum,
                               @Param("classRoomNum") String classRoomNum,
                               @Param("page") Integer page,
                               @Param("rows") Integer rows) {
        IPage<StudyRoomDO> studyRooms = studyRoomBusiness.getStudyRooms(buildingNum, classRoomNum, page, rows);
        return new MyResponseBody("200", "success", studyRooms);
    }

    @PostMapping(value = "/studyroom")
    public MyResponseBody add(@RequestBody @Validated StudyRoomDO studyRoomDO) {
        boolean result = studyRoomBusiness.save(studyRoomDO);
        return new MyResponseBody(result ? "200" : "400", result ? "success" : "failed", result);
    }

    @PutMapping(value = "/studyroom")
    public MyResponseBody save(@RequestBody @Validated({UpdateAction.class, Default.class}) StudyRoomDO studyRoomDO) {
        boolean result = studyRoomBusiness.updateById(studyRoomDO);
        return new MyResponseBody(result ? "200" : "400", result ? "success" : "failed", result);
    }

    @DeleteMapping(value = "/studyroom")
    public MyResponseBody delete(@RequestBody @Validated({UpdateAction.class, Default.class}) StudyRoomDO studyRoomDO) {
        boolean result = studyRoomBusiness.removeById(studyRoomDO);
        return new MyResponseBody(result ? "200" : "400", result ? "success" : "failed", result);
    }
}
