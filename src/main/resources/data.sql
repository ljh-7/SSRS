insert into tbl_student (stuNum, name, password, email)
values ('21210240333', 'Wang FuLong', '123456', '1091876908@qq.com'),
       ('21210240337', 'Wang MingLong', '123456', '2212688995@qq.com'),
       ('21210240334', 'Wang HaoJin', '123456', '1312220946@qq.com'),
       ('21210240134', 'Chen YuQi', '123456', '1378796625@qq.com');
insert into tbl_study_room (buildingNum, classRoomNum)
values ( '1教', '1001' ),
       ( '1教', '1002' ),
       ( '2教', '2001' ),
       ( '2教', '2002' );
insert into tbl_seat (studyRoomId, num, socket)
values ( 1, 2, true ),
       ( 2, 3, false ),
       ( 1, 4, false ),
       ( 2, 5, true );