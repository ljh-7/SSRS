package com.huawei.ibooking.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
public class RoomSeatDo {

    private Integer id;
    private Integer studyRoomId;
    private Integer num;
    private boolean socket;
    private String buildingNum;
    private String classRoomNum;
    private Integer startTime;
    private Integer endTime;


}
