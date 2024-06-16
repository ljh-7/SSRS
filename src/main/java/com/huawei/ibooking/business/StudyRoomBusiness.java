package com.huawei.ibooking.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huawei.ibooking.mapper.StudyRoomMapper;
import com.huawei.ibooking.model.StudyRoomDO;
import com.huawei.ibooking.service.StudyRoomService;
import org.springframework.stereotype.Component;

@Component
public class StudyRoomBusiness extends ServiceImpl<StudyRoomMapper, StudyRoomDO> implements StudyRoomService {
    public IPage<StudyRoomDO> getStudyRooms(String buildingNum, String classRoomNum,
                                            Integer pageNo, Integer size) {
        QueryWrapper<StudyRoomDO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(StringUtils.isNotBlank(buildingNum), "buildingNum", buildingNum)
                .eq(StringUtils.isNotBlank(classRoomNum), "classRoomNum", classRoomNum);
        Page<StudyRoomDO> page = new Page<>(pageNo == null ? 1 : pageNo,
                size == null ? this.count(queryWrapper) : size);
        return this.page(page, queryWrapper);
    }
}
