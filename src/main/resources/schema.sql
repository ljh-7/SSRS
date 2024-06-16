set
foreign_key_checks=0;
drop table if exists tbl_student;
drop table if exists tbl_study_room;
drop table if exists tbl_seat;
drop table if exists tbl_booking;
SET
foreign_key_checks=1;

create table if not exists tbl_student
(
    id       int         not null auto_increment,
    stuNum   varchar(16) not null,
    name     varchar(16) not null,
    password varchar(16) not null,
    credit   int         not null default 0,
    email    varchar(50) not null,
    primary key (id),
    unique (stuNum)
);

create table if not exists tbl_study_room
(
    id           int         not null auto_increment,
    buildingNum  varchar(16) not null,
    classRoomNum varchar(16) not null,
    startTime    int default 7,
    endTime      int default 22,
    primary key (id),
    unique (buildingNum, classRoomNum)
);

create table if not exists tbl_seat
(
    id          int     not null auto_increment,
    studyRoomId int     not null,
    num         int     not null,
    socket      boolean not null default false,
    primary key (id, studyRoomId),
    unique (studyRoomId, num),
    foreign key (studyRoomId) references tbl_study_room (id) on update cascade on delete cascade
);

create table if not exists tbl_booking
(
    id     int      not null auto_increment,
    seatId int      not null,
    stuId  int      not null,
    start  datetime not null,
    end    datetime not null,
    sign   boolean  not null default false,
    state  boolean  not null default TRUE,
    primary key (id),
    foreign key (seatId) references tbl_seat (id) on update cascade on delete cascade,
    foreign key (stuId) references tbl_student (id) on update cascade on delete cascade
);