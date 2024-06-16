package com.huawei.ibooking.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import com.huawei.ibooking.validator.UpdateAction;
import lombok.Data;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
@TableName("tbl_seat")
public class SeatDo {
    @NotNull(groups= UpdateAction.class, message = "不能为空")
    private Integer id;
    @NotNull(message = "不能为空")
    private Integer studyRoomId;
    @NotNull(message = "不能为空")
    private Integer num;
    @NotNull(message = "不能为空")
    private boolean socket;
}
