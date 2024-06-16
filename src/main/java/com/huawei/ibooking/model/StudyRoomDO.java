package com.huawei.ibooking.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import com.huawei.ibooking.validator.UpdateAction;
import com.huawei.ibooking.validator.ValidTimeRange;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
@TableName("tbl_study_room")
@ValidTimeRange
public class StudyRoomDO {
    @NotNull(groups = UpdateAction.class, message = "不能为空")
    private Integer id;
    @NotNull(message = "不能为空")
    @Length(max = 16, min = 1, message = "长度在16个字符以内")
    private String buildingNum;
    @NotNull(message = "不能为空")
    @Length(max = 16, min = 1, message = "长度在16个字符以内")
    private String classRoomNum;
    @NotNull(message = "不能为空")
    @Range(min = 0, max = 24, message = "应在0-24之间")
    private Integer startTime;
    @NotNull(message = "不能为空")
    @Range(min = 0, max = 24, message = "应在0-24之间")
    private Integer endTime;
}
