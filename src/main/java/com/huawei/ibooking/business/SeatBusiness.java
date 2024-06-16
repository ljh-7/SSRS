package com.huawei.ibooking.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huawei.ibooking.mapper.SeatMapper;
import com.huawei.ibooking.model.SeatDo;
import com.huawei.ibooking.service.SeatService;
import org.springframework.stereotype.Service;

@Service
public class SeatBusiness extends ServiceImpl<SeatMapper, SeatDo> implements SeatService {

    public Page<SeatDo> getSeats(String studyRoomId, Integer num, Boolean socket, Integer pageNo, Integer size) {
        QueryWrapper<SeatDo> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(StringUtils.isNotBlank(studyRoomId), "studyRoomId", studyRoomId)
                .eq(socket != null, "socket", socket)
                .eq(num != null, "num", num);
        Page<SeatDo> page = new Page<>(pageNo == null? 1 : pageNo,
                size == null ? this.count(queryWrapper) : size);
        return this.page(page, queryWrapper);
    }

}
