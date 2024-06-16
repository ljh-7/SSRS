package com.huawei.ibooking.mapper;

import com.huawei.ibooking.model.BookingDo;
import com.huawei.ibooking.model.SeatDo;
import com.huawei.ibooking.model.StudentDO;
import com.huawei.ibooking.model.StudyRoomDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookingMapper {
    List<BookingDo> listAllBooks();

    int saveBookingInfo(@Param("booking") BookingDo booking);

    int updateBookingSign(@Param("booking") BookingDo bookingDo);

    int updateBookingState(@Param("booking") BookingDo bookingDo);

    int deleteBooking(@Param("booking") BookingDo bookingDo);


    List<SeatDo> getSeats();


    List<StudyRoomDO> getStudyRooms();


    List<StudentDO> getStudentDos();

}
