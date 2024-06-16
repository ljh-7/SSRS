package com.huawei.ibooking.business;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huawei.ibooking.dao.BookingDao;
import com.huawei.ibooking.model.*;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class BookingBusiness {
    @Autowired
    private BookingDao bookingDao;


    @Autowired
    private StudentBusiness studentBusiness;

    @Autowired
    private StudyRoomBusiness studyRoomBusiness;

    @Autowired
    private SeatBusiness seatBusiness;


    // private Consumer consumer = new Consumer();
    public BookingBusiness() throws MQClientException {
    }

    public List<BookingDo> listAllBooks(StudentDO stu, Boolean status, Boolean sign, Integer seatId) {
        List<BookingDo> bookingDos = bookingDao.listAllBooks();

        if (stu.getId() != null) {
            for (int i = 0; i < bookingDos.size(); ++i) {
                if (bookingDos.get(i).getStuId() != stu.getId()) {
                    bookingDos.remove(i);
                    i--;
                }
            }
        }

        if (status != null) {
            for (int i = 0; i < bookingDos.size(); ++i) {
                if (bookingDos.get(i).isState() != status) {
                    bookingDos.remove(i);
                    i--;
                }
            }
        }

        if (seatId != null) {
            for (int i = 0; i < bookingDos.size(); ++i) {
                if (bookingDos.get(i).getSeatId() != seatId) {
                    bookingDos.remove(i);
                    i--;
                }
            }
        }

        if (sign != null) {
            for (int i = 0; i < bookingDos.size(); ++i) {
                if (bookingDos.get(i).isSign() != sign) {
                    bookingDos.remove(i);
                    i--;
                }
            }
        }
        return bookingDos;
    }


    public String addBooking(BookingDo bookingDo) throws UnsupportedEncodingException, MQBrokerException, RemotingException, InterruptedException, MQClientException {


        List<BookingDo> signedSeatDos = bookingDao.listAllBooks();

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        int flag = 0;

        for (int i = 0; i < signedSeatDos.size(); ++i) {
            if (signedSeatDos.get(i).getSeatId() == bookingDo.getSeatId()) {

                boolean result = fmt.format(bookingDo.getStart()).equals(fmt.format(signedSeatDos.get(i).getStart()));

                if (result) {
                    boolean result1 = (bookingDo.getStart().getHours() < signedSeatDos.get(i).getEnd().getHours() && bookingDo.getStart().getHours() >= signedSeatDos.get(i).getStart().getHours());

                    boolean result2 = (bookingDo.getEnd().getHours() <= signedSeatDos.get(i).getEnd().getHours() && bookingDo.getEnd().getHours() > signedSeatDos.get(i).getStart().getHours());

                    if (result1 || result2) {
                        if (signedSeatDos.get(i).isState()) {
                            flag = 1;
                        }
                    }


                }
            }
        }

        if (flag == 1) {
            return "不符合自习室的开放时间";
        }


        List<StudyRoomDO> studyRoomDOS = bookingDao.getStudyRoomDos();

        int num = bookingDo.getSeatId();
        int studyNum = 0;
        List<SeatDo> seatDos = bookingDao.getSeats();
        for (int i = 0; i < seatDos.size(); ++i) {
            if (seatDos.get(i).getId() == num) {
                studyNum = seatDos.get(i).getStudyRoomId();
                break;
            }
        }
        for (int i = 0; i < studyRoomDOS.size(); ++i) {

            if (studyRoomDOS.get(i).getId() == studyNum) {
                if ((bookingDo.getStart().getHours() < studyRoomDOS.get(i).getStartTime()) || (bookingDo.getEnd().getHours() > studyRoomDOS.get(i).getEndTime())) {
                    flag = 1;
                    break;
                }
            }
        }

        if (flag == 1) {
            return "此时间段已经被预约";
        }



        return bookingDao.addBooking(bookingDo);
    }


    public String updateBookingSign(BookingDo bookingDo) {
        return bookingDao.updateBookingSign(bookingDo);
    }


    public String updateBookingState(BookingDo bookingDo) {
        return bookingDao.updateBookingState(bookingDo);
    }


    public String deleteBooking(BookingDo bookingDo) {
        return bookingDao.deleteBooking(bookingDo);
    }


    public List<SeatDo> getAvailSeats(Integer studyRoomId, Date start, Date end, Boolean socket) {

        List<SeatDo> seatDos = bookingDao.getSeats();
        List<StudyRoomDO> studyRoomDOS = bookingDao.getStudyRoomDos();

        for (int i = 0; i < seatDos.size(); ++i) {
            int num = seatDos.get(i).getStudyRoomId();

            for (int j = 0; j < studyRoomDOS.size(); ++j) {
                if (num == studyRoomDOS.get(j).getId()) {
                    if (start.getHours() < studyRoomDOS.get(j).getStartTime() || end.getHours() > studyRoomDOS.get(j).getEndTime()) {
                        seatDos.remove(i);
                        --i;
                    }
                    if (start.getHours() >= studyRoomDOS.get(j).getEndTime()) {
                        seatDos.remove(i);
                        --i;
                    }
                    break;
                }
            }


        }

        if (studyRoomId != null) {
            for (int i = 0; i < seatDos.size(); ++i) {
                if (!Objects.equals(seatDos.get(i).getStudyRoomId(), studyRoomId)) {
                    seatDos.remove(i);
                    --i;
                }
            }
        }

        if (socket != null) {
            for (int i = 0; i < seatDos.size(); ++i) {
                if (seatDos.get(i).isSocket() != socket) {
                    seatDos.remove(i);
                    --i;
                }
            }
        }

        List<SeatDo> availDos = new ArrayList<>();
        List<BookingDo> signedSeatDos = bookingDao.listAllBooks();
        List<Integer> signedSeatId = new ArrayList<>();

        for (int j = 0; j < signedSeatDos.size(); ++j) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

            boolean result = fmt.format(start).equals(fmt.format(signedSeatDos.get(j).getStart()));

            if (result) {

                boolean result1 = (end.getHours() <= signedSeatDos.get(j).getEnd().getHours() && end.getHours() > signedSeatDos.get(j).getStart().getHours());

                boolean result2 = (start.getHours() < signedSeatDos.get(j).getEnd().getHours() && start.getHours() >= signedSeatDos.get(j).getStart().getHours());


                boolean result3 = (start.getHours() <= signedSeatDos.get(j).getStart().getHours() && end.getHours() >= signedSeatDos.get(j).getEnd().getHours());
//                boolean result1 = (signedSeatDos.get(j).getEnd().getHours() < end.getHours() && signedSeatDos.get(j).getEnd().getHours() > start.getHours());
//                boolean result2 = (signedSeatDos.get(j).getStart().getHours() < end.getHours() && signedSeatDos.get(j).getStart().getHours() > start.getHours());

                if (result1 || result2 || result3) {
                    if (signedSeatDos.get(j).isState()) {
                        signedSeatId.add(signedSeatDos.get(j).getSeatId());
                    }
                }
            }
        }
        for (int i = 0; i < seatDos.size(); ++i) {
            int flag = 0;
            for (int j = 0; j < signedSeatId.size(); ++j) {
                if (Objects.equals(seatDos.get(i).getId(), signedSeatId.get(j))) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                availDos.add(seatDos.get(i));
            }
        }


        return availDos;
    }

    public List<List<Integer>> getAvailSeatDate(Integer seatId, Date date) {

        List<List<Integer>> resDateSeat = new ArrayList<>();
        List<BookingDo> signedSeatDos = bookingDao.listAllBooks();
        List<StudyRoomDO> studyRoomDOS = bookingDao.getStudyRoomDos();
        List<Integer> startHour = new ArrayList<>();
        List<Integer> endHour = new ArrayList<>();
        List<SeatDo> seatDos = bookingDao.getSeats();


        int roomId = 0;

        int flag = 0;
        for (int i = 0; i < seatDos.size(); ++i) {
            if (Objects.equals(seatId, seatDos.get(i).getId())) {
                flag = 1;
            }
        }
        if (flag == 0) {
            resDateSeat.add(Arrays.asList(0, 0));
            return resDateSeat;
        }

        for (int i = 0; i < seatDos.size(); ++i) {
            if (Objects.equals(seatId, seatDos.get(i).getId())) {
                roomId = seatDos.get(i).getStudyRoomId();
            }
        }
        int roomStartTime = 0;
        int roomEndTime = 0;

        for (int i = 0; i < studyRoomDOS.size(); ++i) {
            if (studyRoomDOS.get(i).getId() == roomId) {
                roomStartTime = studyRoomDOS.get(i).getStartTime();
                roomEndTime = studyRoomDOS.get(i).getEndTime();
                break;
            }
        }


        for (int j = 0; j < signedSeatDos.size(); ++j) {

            if (signedSeatDos.get(j).getSeatId() == seatId) {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

                boolean result = fmt.format(date).equals(fmt.format(signedSeatDos.get(j).getStart()));
                if (result) {
                    if (signedSeatDos.get(j).isState()) {
                        startHour.add(signedSeatDos.get(j).getStart().getHours());
                        endHour.add(signedSeatDos.get(j).getEnd().getHours());
                    }
                }
            }
        }


        if (startHour.size() == 0) {
//                    Date startDate =new Date();
            Date endDate = (Date) date.clone();
            Date startDate = (Date) date.clone();
            startDate.setHours(roomStartTime);
            endDate.setHours(roomEndTime);
            Date dateNow = new Date();


            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

            boolean result = fmt.format(dateNow).equals(fmt.format(startDate));


            if (result) {
                if (startDate.getHours() < dateNow.getHours() && endDate.getHours() < dateNow.getHours()) {

                } else if (startDate.getHours() < dateNow.getHours() && endDate.getHours() > dateNow.getHours()) {
                    resDateSeat.add(Arrays.asList(dateNow.getHours(), endDate.getHours()));
                } else {
                    resDateSeat.add(Arrays.asList(startDate.getHours(), endDate.getHours()));
                }
            } else {
                resDateSeat.add(Arrays.asList(startDate.getHours(), endDate.getHours()));
            }
            if (resDateSeat.get(resDateSeat.size() - 1).get(1) == 0) {
                resDateSeat.get(0).set(1, 24);
            }

            return resDateSeat;
        }

        Collections.sort(startHour);
        Collections.sort(endHour);

        if (!startHour.equals(roomStartTime)) {
            Date endDate = (Date) date.clone();
            Date startDate = (Date) date.clone();
            startDate.setHours(roomStartTime);
            endDate.setHours(startHour.get(0));
            resDateSeat.add(Arrays.asList(startDate.getHours(), endDate.getHours()));
        }

        for (int i = 0; i < startHour.size() - 1; ++i) {
            Date endDate = (Date) date.clone();
            Date startDate = (Date) date.clone();

            startDate.setHours(endHour.get(i));
            endDate.setHours(startHour.get(i + 1));
            resDateSeat.add(Arrays.asList(startDate.getHours(), endDate.getHours()));
        }

        if (!endHour.equals(roomEndTime)) {
            Date endDate = (Date) date.clone();
            Date startDate = (Date) date.clone();
            startDate.setHours(endHour.get(startHour.size() - 1));
            endDate.setHours(roomEndTime);
            resDateSeat.add(Arrays.asList(startDate.getHours(), endDate.getHours()));
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        Date dateNow = new Date();
        if (fmt.format(date).equals(fmt.format(dateNow))) {
            for (int i = 0; i < resDateSeat.size(); ++i) {
                if (resDateSeat.get(i).get(0) <= dateNow.getHours() && resDateSeat.get(i).get(1) <= dateNow.getHours()) {
                    resDateSeat.remove(i);
                    --i;
                } else if (resDateSeat.get(i).get(0) < dateNow.getHours() && resDateSeat.get(i).get(1) > dateNow.getHours()) {
                    resDateSeat.get(i).set(0, dateNow.getHours());
                }
            }
        }

        for (int i = 0; i < resDateSeat.size(); ++i) {
            if (Objects.equals(resDateSeat.get(i).get(0), resDateSeat.get(i).get(1))) {
                resDateSeat.remove(i);
                --i;
            }
        }


        if (resDateSeat.get(resDateSeat.size() - 1).get(1) == 0) {
            resDateSeat.get(resDateSeat.size() - 1).set(1, 24);
        }


        return resDateSeat;
    }

    public List<RoomSeatDo> getAllInfoByStuId(Integer studyRoomId, Date start, Date end, Boolean socket) {

        List<SeatDo> seatDos = getAvailSeats(studyRoomId, start, end, socket);
        List<StudyRoomDO> studyRoomDOS = bookingDao.getStudyRoomDos();
        List<RoomSeatDo> res = new ArrayList<>();
        for (int i = 0; i < seatDos.size(); ++i) {
            for (int j = 0; j < studyRoomDOS.size(); ++j) {
                if (Objects.equals(seatDos.get(i).getStudyRoomId(), studyRoomDOS.get(j).getId())) {
                    res.add(new RoomSeatDo(seatDos.get(i).getId(), seatDos.get(i).getStudyRoomId(), seatDos.get(i).getNum(), seatDos.get(i).isSocket(), studyRoomDOS.get(j).getBuildingNum(), studyRoomDOS.get(j).getClassRoomNum(), studyRoomDOS.get(j).getStartTime(), studyRoomDOS.get(j).getEndTime()));
                }
            }
        }
        return res;
    }

}


