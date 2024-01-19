# 아싸를 위한 온라인 커뮤니티 – “친구 찾기”

사이트를 실제로 실행하기 위해서는 먼저 mariaDB와 환경변수 설정이 필요합니다.

## DB 기본 설정
DB는 mariaDB를 사용해 주세요. 포트는 9933, DB 이름은 uni_meeting으로 설정하세요.

## DB 테이블 설정
 DB 테이블 초기 설정을 위해 필요한 쿼리 구문은 다음과 같습니다.

``` sql
create sequence seq_board nocache

create table tbl_board (
    BNO int(10),
    TITLE varchar(200) not null,
    CONTENT varchar(2000) not null,
    MID int(20) not null,
    SCHOOL_NAME varchar(50) not null,
    BOARD_ID int not null,
    EDITED int(1) not null default 0,
    REGDATE date default sysdate(),
    UPDATEDATE date default sysdate(),
    PLACE_ID int(10) default null
)

alter table
    tbl_board
add
    constraint tbl_board_pk primary key (BNO)


create table tbl_school (
    SCHOOL_ID varchar(50) not null,
    SCHOOL_NAME_KOR varchar(50) not null,
    REGDATE date default sysdate(),
    UPDATEDATE date default sysdate()
)

alter table
    tbl_school
add
    constraint tbl_school_pk primary key (School_id)

alter table
    tbl_school change school_name_kor SCHOOL_NAME_KOR varchar(50)

insert into tbl_school (school_id, school_name)
values ("konkuk", "건국대")

insert into tbl_school (school_id, school_name)
values ("snu", "서울대")

insert into tbl_school (school_id, school_name_kor)
values ("yonsei", "연세대")

insert into tbl_school (school_id, school_name_kor)
values ("cau", "중앙대")


create table tbl_comment(
    cno int(10),
    bno int(10) not null,
    mid int(20) not null,
    comment_content varchar(2000) not null,
    target_cno int(10),
    target_mid int(20),
    edited bit(1) default 0,
    regdate date default sysdate(),
    updatedate date default sysdate(),
    secret bit(1) not null default 0,
    del bit(1) not null default 0,
    primary key(cno)
)

create sequence seq_comment nocache;
```


## 환경변수 설정
1. UNI_MEETING_DB_USERNAME : mariaDB의 username
2. UNI_MEETING_DB_PASSWORD : mariaDB의 password
3. KAKAO_REST : kakao developers 에서 받은 REST API 키
4. KAKAO_JAVASCRIPT : kakao developers에서 받은 JavaScript 키
5. KAKAO_SECRET : kakao developers -> 내 애플리케이션 -> 카카오 로그인 -> 보안 -> Client secret 에 있는 코드 입력


