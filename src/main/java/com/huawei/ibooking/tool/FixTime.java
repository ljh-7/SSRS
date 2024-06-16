package com.huawei.ibooking.tool;

import com.huawei.ibooking.business.SeatBusiness;
import com.huawei.ibooking.business.StudentBusiness;
import com.huawei.ibooking.business.StudyRoomBusiness;
import com.huawei.ibooking.dao.BookingDao;
import com.huawei.ibooking.model.BookingDo;
import com.huawei.ibooking.model.SeatDo;
import com.huawei.ibooking.model.StudentDO;
import com.huawei.ibooking.model.StudyRoomDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.mail.MessagingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Configuration
@EnableScheduling
@EnableAsync
public class FixTime {

    @Autowired
    public BookingDao bookingDao;
    @Autowired
    private StudentBusiness studentBusiness;

    @Autowired
    private StudyRoomBusiness studyRoomBusiness;

    @Autowired
    private SeatBusiness seatBusiness;

    @Async
    @Scheduled(cron = "0 55 * * * ?")
    public void sendemail() throws MessagingException, GeneralSecurityException {
        List<BookingDo> bookingDos = bookingDao.listAllBooks();
        for (BookingDo bookingDao : bookingDos) {
            long future = bookingDao.getStart().getTime();
            long now = new Date().getTime();
            System.out.println("第一次提醒");
            if (now-future < 301 * 1000&&now-future>120*1000&&bookingDao.isState()==true){
                StudentDO studentbyId = studentBusiness.getById(bookingDao.getStuId());
                SeatDo seatbyId1 = seatBusiness.getById(bookingDao.getSeatId());
                int seatnum = seatbyId1.getNum();
                StudyRoomDO studyroombyId = studyRoomBusiness.getById(seatbyId1.getStudyRoomId());
                String buildernum = studyroombyId.getBuildingNum();
                String classnum = studyroombyId.getClassRoomNum();
                String eamil = studentbyId.getEmail();
                String name = studentbyId.getName();
                email.sendEmail(eamil, name + "同学你在" + buildernum + "的" + classnum + "教室" + "编号为" + seatnum + "的座位还有5分钟即可使用");
                System.out.println("邮件发送成功");
            }

        }

    }

    @Async
    @Scheduled(cron = "0 15 * * * ?")
    public void deleteInvalidBooking() throws MessagingException, GeneralSecurityException {
        List<BookingDo> bookingDos = bookingDao.listAllBooks();
        System.out.println("跟新");
        for (BookingDo bookingDa : bookingDos) {
            long future = bookingDa.getStart().getTime();
            long now = new Date().getTime();
            if (future - now >= 900 * 1000) {
                //更新
                if(bookingDa.isSign()==false){
                    bookingDa.setState(false);
                    StudentDO studentbyId = studentBusiness.getById(bookingDa.getStuId());
                    Integer credit =  studentbyId.getCredit();
                    credit++;
                    studentbyId.setCredit(credit);
                    studentBusiness.updateById(studentbyId);
                    bookingDao.updateBookingSign(bookingDa);
                }
            }
        }

    }
    @Async
    @Scheduled(cron = "0 0 * * * ?")
    public void  deleteCompleteBooking(){
        List<BookingDo> bookingDos = bookingDao.listAllBooks();
        for (BookingDo bookingDa : bookingDos) {
            long future = bookingDa.getEnd().getTime();
            long now = new Date().getTime();
            if (future<=now) {
                bookingDa.setState(false);
                StudentDO studentbyId = studentBusiness.getById(bookingDa.getStuId());

                bookingDao.updateBookingState(bookingDa);
            }
        }
    }
    @Scheduled(cron = "0 10 * * * ?")
    public void  remainAgain() throws MessagingException, GeneralSecurityException {
        List<BookingDo> bookingDos = bookingDao.listAllBooks();
        for (BookingDo bookingDao : bookingDos) {
            long future = bookingDao.getStart().getTime();
            long now = new Date().getTime();
            if (now -future <=601 * 1000&&now-future>550*1000&&bookingDao.isState()==true) {
                StudentDO studentbyId = studentBusiness.getById(bookingDao.getStuId());
                SeatDo seatbyId1 = seatBusiness.getById(bookingDao.getSeatId());
                int seatnum = seatbyId1.getNum();
                StudyRoomDO studyroombyId = studyRoomBusiness.getById(seatbyId1.getStudyRoomId());
                String buildernum = studyroombyId.getBuildingNum();
                String classnum = studyroombyId.getClassRoomNum();
                String eamil = studentbyId.getEmail();
                String name = studentbyId.getName();
                email.sendEmail(eamil, name + "同学你在" + buildernum + "的" + classnum + "教室" + "编号为" + seatnum + "的座位即将过期了");
                System.out.println("邮件发送成功");
            }

        }
    }
}
