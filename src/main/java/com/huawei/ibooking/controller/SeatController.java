package com.huawei.ibooking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.ibooking.business.SeatBusiness;
import com.huawei.ibooking.validator.UpdateAction;
import com.huawei.ibooking.model.MyResponseBody;
import com.huawei.ibooking.model.SeatDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;

@RestController
public class SeatController {
    @Autowired
    private SeatBusiness seatBusiness;

    /**
     * 根据自习室ID和是否有插座筛选座位，参数可为null
     *
     * @param studyRoomId
     * @param socket
     * @return
     */
    @GetMapping(value = "/seat")
    public MyResponseBody list(@Param("studyRoomId") String studyRoomId,
                               @Param("num") Integer num,
                               @Param("socket") Boolean socket,
                               @Param("page") Integer page,
                               @Param("rows") Integer rows) {
        final Page<SeatDo> res = seatBusiness.getSeats(studyRoomId, num, socket, page, rows);
        return new MyResponseBody("200", "success", res);
    }

    @PostMapping(value = "/seat")
    public MyResponseBody add(@RequestBody @Validated SeatDo seat) {
        boolean result = seatBusiness.save(seat);
        return new MyResponseBody(result ? "200" : "400", result ? "success" : "failed", result);
    }

    @PutMapping(value = "/seat")
    public MyResponseBody save(@RequestBody @Validated({UpdateAction.class, Default.class}) SeatDo seat) {
        boolean result = seatBusiness.updateById(seat);
        return new MyResponseBody(result ? "200" : "400", result ? "success" : "failed", result);
    }

    @DeleteMapping(value = "/seat")
    public MyResponseBody delete(@RequestBody @Validated({UpdateAction.class, Default.class}) SeatDo seat) {
        boolean result = seatBusiness.removeById(seat);
        return new MyResponseBody(result ? "200" : "400", result ? "success" : "failed", result);
    }
}
