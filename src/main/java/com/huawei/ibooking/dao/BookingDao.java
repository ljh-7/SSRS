package com.huawei.ibooking.dao;

import com.huawei.ibooking.mapper.BookingMapper;
import com.huawei.ibooking.model.BookingDo;
import com.huawei.ibooking.model.SeatDo;
import com.huawei.ibooking.model.StudentDO;
import com.huawei.ibooking.model.StudyRoomDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
public class BookingDao {

    @Autowired
    private BookingMapper bookingMapper;


    public List<BookingDo> listAllBooks() {
        return bookingMapper.listAllBooks();
    }


    public String addBooking(BookingDo bookingDo) {
        String s = "";
        try {
            bookingMapper.saveBookingInfo(bookingDo);
            return s;
        } catch (Exception e) {
            return e.getCause() + "";
        }
    }


    public String updateBookingSign(BookingDo bookingDo) {

        if (bookingMapper.updateBookingSign(bookingDo) > 0) {
            return "";
        } else {
            return "更新参数未匹配";
        }
    }

    public String updateBookingState(BookingDo bookingDo) {
        if (bookingMapper.updateBookingState(bookingDo) > 0) {
            return "";
        } else {
            return "更新参数未匹配";
        }
    }

    public String deleteBooking(BookingDo bookingDo) {

        if (bookingMapper.deleteBooking(bookingDo) > 0) {
            return "";
        } else {
            return "删除参数未匹配";
        }
    }

    public List<SeatDo> getSeats() {
        return bookingMapper.getSeats();
    }

    public List<StudyRoomDO> getStudyRoomDos() {
        return bookingMapper.getStudyRooms();
    }


    public List<StudentDO> getStudentDos() {
        return bookingMapper.getStudentDos();
    }

}
