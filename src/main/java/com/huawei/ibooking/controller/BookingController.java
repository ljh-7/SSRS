package com.huawei.ibooking.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huawei.ibooking.business.BookingBusiness;
import com.huawei.ibooking.interceptor.LoginInterceptor;
import com.huawei.ibooking.model.*;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class BookingController {
    @Autowired
    private BookingBusiness bookingBusiness;


    @GetMapping(value = "/ibooking")
    public MyResponseBody listAllBooks(HttpServletRequest request, @Param("stu") StudentDO stu, @Param("state") Boolean state, @Param("sign") Boolean sign, @Param("seatId") Integer seatId) {
        HttpSession session = request.getSession();
        StudentDO user = (StudentDO) session.getAttribute("user");
        int userId = user.getId();
        final List<BookingDo> bookingDos = bookingBusiness.listAllBooks(stu, state, sign, seatId);
        for (int i = 0; i < bookingDos.size(); ++i) {
            if (bookingDos.get(i).getStuId() != userId) {
                bookingDos.remove(i);
                --i;
            }
        }

        return new MyResponseBody("200", "success", bookingDos);

    }


    @PostMapping(value = "/ibooking")
    public MyResponseBody addBooking(@RequestBody BookingDo bookingDo, HttpServletRequest request) throws UnsupportedEncodingException, MQBrokerException, RemotingException, InterruptedException, MQClientException {
        HttpSession session = request.getSession();
        StudentDO user = (StudentDO) session.getAttribute("user");
        int userId = user.getId();
        if (userId != bookingDo.getStuId()) {
            return new MyResponseBody("400", "参数错误", "学生验证不通过");
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        boolean res = fmt.format(bookingDo.getStart()).equals(fmt.format(bookingDo.getEnd()));

        if (!VerDate(bookingDo.getStart()) || !VerDate(bookingDo.getEnd()) || bookingDo.getEnd().before(bookingDo.getStart()) || !res) {
            return new MyResponseBody("400", "参数错误", "时间校验不通过");
        }


        String result = bookingBusiness.addBooking(bookingDo);


        if (result.equals("")) {
            return new MyResponseBody("200", "success", result);
        } else {
            return new MyResponseBody("400", "failed", result);
        }

    }


    @PutMapping(value = "/ibooking")
    public MyResponseBody updateBookingSign(@RequestBody BookingDo bookingDo, HttpServletRequest request) {
        HttpSession session = request.getSession();
        StudentDO user = (StudentDO) session.getAttribute("user");
        int userId = user.getId();
        if (userId != bookingDo.getStuId()) {
            return new MyResponseBody("400", "参数错误", "学生验证不通过");
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        boolean res = fmt.format(bookingDo.getStart()).equals(fmt.format(bookingDo.getEnd()));

        if (!VerDate(bookingDo.getStart()) || !VerDate(bookingDo.getEnd()) || bookingDo.getEnd().before(bookingDo.getStart()) || !res) {
            return new MyResponseBody("400", "参数错误", "时间校验不通过");
        }
        String result = bookingBusiness.updateBookingSign(bookingDo);
        if (result.equals("")) {
            return new MyResponseBody("200", "success", null);
        } else {
            return new MyResponseBody("400", "failed", result);
        }
    }


    @PutMapping(value = "/ibookingState")
    public MyResponseBody updateBookingState(@RequestBody BookingDo bookingDo, HttpServletRequest request) {
        HttpSession session = request.getSession();
        StudentDO user = (StudentDO) session.getAttribute("user");
        int userId = user.getId();
        if (userId != bookingDo.getStuId()) {
            return new MyResponseBody("400", "参数错误", "学生验证不通过");
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        boolean res = fmt.format(bookingDo.getStart()).equals(fmt.format(bookingDo.getEnd()));

        if (!VerDate(bookingDo.getStart()) || !VerDate(bookingDo.getEnd()) || bookingDo.getEnd().before(bookingDo.getStart()) || !res) {
            return new MyResponseBody("400", "参数错误", "时间校验不通过");
        }
        String result = bookingBusiness.updateBookingState(bookingDo);

        if (result.equals("")) {
            return new MyResponseBody("200", "success", null);
        } else {
            return new MyResponseBody("400", "failed", result);
        }
    }

    @DeleteMapping(value = "/ibooking")
    public MyResponseBody deleteBooking(@RequestBody BookingDo bookingDo, HttpServletRequest request) {
        HttpSession session = request.getSession();
        StudentDO user = (StudentDO) session.getAttribute("user");
        int userId = user.getId();
        if (userId != bookingDo.getStuId()) {
            return new MyResponseBody("400", "参数错误", "学生验证不通过");
        }
        String result = bookingBusiness.deleteBooking(bookingDo);

        if (result.equals("")) {
            return new MyResponseBody("200", "success", null);
        } else {
            return new MyResponseBody("400", "failed", result);
        }

    }

    @GetMapping(value = "/ibooking/seat")
    public MyResponseBody getAvailSeats(@Param("socket") Boolean socket, @Param("studyRoomId") Integer studyRoomId, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") @Param("startTime") Date startTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") @Param("endTime") Date endTime) {

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        boolean res = fmt.format(startTime).equals(fmt.format(endTime));

        if (!VerDate(startTime) || !VerDate(endTime) || endTime.before(startTime) || !res) {
            return new MyResponseBody("400", "参数错误", "时间校验不通过");
        }

        List<RoomSeatDo> seatDos = bookingBusiness.getAllInfoByStuId(studyRoomId, startTime, endTime, socket);

        return new MyResponseBody("200", "success", seatDos);
    }

    @GetMapping(value = "/ibooking/dateSeat")
    public MyResponseBody getAvailDateSeats(@Param("seatId") Integer seatId, @DateTimeFormat(pattern = "yyyy-MM-dd") @Param("time") Date startTime) {
        startTime.setHours(23);
        startTime.setMinutes(59);
        startTime.setMinutes(59);

        if (!VerDate(startTime)) {
            return new MyResponseBody("400", "参数错误", "时间校验不通过");
        }

        List<List<Integer>> dateSeat = bookingBusiness.getAvailSeatDate(seatId, startTime);

        if (dateSeat.get(0).get(1) == 0) {
            return new MyResponseBody("400", "参数错误", "没有此座位");
        }

        return new MyResponseBody("200", "success", dateSeat);
    }


    private Boolean VerDate(Date dateTest) {

        Date date = new Date();
        if (dateTest.before(date)) {
            return false;
        }
        return true;
    }


}
