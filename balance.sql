-- instagram connect --> token and api call
-- picture max 5
-- user, liked, setting, user_picture,


-- find srid of column
-- SELECT Find_SRID('public', 'users', 'location');
--
-- -- find srid of column
-- select st_srid(location)
-- from public.geography_users;
--
-- -- get lon and lat from point
-- SELECT ST_X(location), ST_Y(location)
-- FROM users;
--
-- -- distance in meters between two points
-- select ST_DistanceSphere(location, st_setsrid(st_point(126.970769, 37.555479), 4326))
-- from users;
--
-- -- print out values in procedure
-- RAISE NOTICE 'i want to print % and %', lat_count, start_lon;
--
-- -- list of srid supported by postgis
-- select *
-- from spatial_ref_sys
-- where srid > 5180
--   and srid < 5200;
--
-- -- find details of srid
-- SELECT srtext
-- FROM spatial_ref_sys
-- WHERE srid = 4326;
--
--
--
-- drop table users;
--
-- create table users
-- (
--     id         serial primary key,
--     location   geography(POINT, 4326) not null,
--     birth_year int                    not null,
--     birth      date                   not null,
--     gender     int                    not null,
--     liked      int                    not null
-- );
--
-- CREATE INDEX users_location_idx ON users USING GIST (location);
--
--
--
create or replace procedure populate_user()
    language plpgsql as
$$
declare
    start_lat     numeric(8, 6) := 37.463557;
    end_lat       numeric(8, 6) := 37.650017;
    start_lon     numeric(9, 6) := 126.807883;
    end_lon       numeric(9, 6) := 127.176639;
    lat_count     numeric(8, 6) := start_lat;
    max_duplicate int           := 0;
    inner_count   int           := 0;
    total_count   int           := 0;
    random_gender int;
    varchar_date  varchar(50);
    v_gender      boolean;
begin

    while start_lon < end_lon
        loop
            start_lon := start_lon + 0.000300;
            while lat_count < end_lat
                loop
                    max_duplicate := round((random() * (7 - 1)) + 1);
                    while inner_count <= max_duplicate
                        loop

                            varchar_date :=
                                    concat(round((random() * (2003 - 1970)) + 1970)::varchar, '/', round((random() * (12 - 1)) + 1)::varchar,
                                           '/', round((random() * (27 - 1)) + 1)::varchar);

                            random_gender := round((random() * (1 - 0)) + 0);

                            if random_gender = 1 then v_gender = false; else v_gender = true; end if;

                            insert into account
                            values (uuid_generate_v4(), false, 'account', 'account',
                                    to_date(varchar_date, 'YYYY/MM/DD'), 'this is about', v_gender,
                                    round((random() * (100000 - 0)) + 0), round((random() * (100 - 0)) + 0), 10, 10,
                                    current_timestamp, ST_MakePoint(start_lon, lat_count),
                                    1, current_timestamp, current_timestamp);

                            --                             values (default,
--                                     ST_MakePoint(start_lon, lat_count),
--                                     year,
--                                     to_date(varchar_date, 'YYYYMMDD'),
--                                     round((random() * (1 - 0)) + 0),
--                                     round((random() * (100000 - 0)) + 0));
                            total_count := total_count + 1;
                            lat_count := lat_count + 0.000010;
                            inner_count := inner_count + 1;
                        end loop;
                    inner_count := 0;
                    lat_count := lat_count + 0.000300;
                end loop;
            lat_count := start_lat;
        end loop;

    RAISE NOTICE 'total count: %', total_count;
end
$$;
--
call populate_user();
--
-- select count(*)
-- from users;
--
--


-- BALANCE-TALK DATABASE


-- setting: distance, age, gender will be included in query from front end, dont have to create a table for settings
-- normal like does not cost, heart like costs, watch ad every 15 user cards, change question costs, add question does not costs


drop table unblock;
drop table report_resolution;
drop table deleted_photo;
drop table report;
drop table report_reason;
drop table report_resolution_type;
drop table admin;


drop table account_question;
drop table question;
drop table photo;
drop table match;
drop table swipe;
drop table sent_chat_message;
drop table chat_message;
drop table chat;
drop table login;
drop table push_token;
drop table profile;
drop table report;
drop table wallet;
drop table account;


-- TODO: 2020-09-14 execute these

CREATE EXTENSION if not exists postgis;
-- create UUID extension
create extension if not exists "uuid-ossp";


-- unregister deletes account
-- liked count will be reset on every night
create table account
(
    version           int         not null,
    id                uuid primary key,
    identity_token    uuid        not null,
    name              varchar(15) not null,
    profile_photo_key varchar(40) not null,
    blocked           boolean     not null,
    deleted           boolean     not null,
    created_at        timestamptz not null,
    updated_at        timestamptz not null
);

create table role
(
    id   serial primary key,
    name varchar(30) not null
);

insert into role
values (default, 'ADMIN');
-- insert into role
-- values (default, 'USER');



create table account_role
(
    account_id uuid not null,
    role_id    int  not null,

    primary key (account_id, role_id),
    constraint account_role_account_id_fk foreign key (account_id) references account (id),
    constraint account_role_role_id_fk foreign key (role_id) references role (id)
);

create table wallet
(
    account_id              uuid primary key,
    point                   int         not null,
    free_swipe              int         not null,
    free_swipe_recharged_at timestamptz not null,

    constraint wallet_account_id_fk foreign key (account_id) references account (id)
);



create table question
(
    id            serial primary key,
    description   varchar(200) not null,
    top_option    varchar(50)  not null,
    bottom_option varchar(50)  not null,
    created_at    timestamptz  not null,
    updated_at    timestamptz  not null
);

create table account_question
(
    account_id  uuid        not null,
    question_id int         not null,
    selected    boolean     not null,
    answer      boolean     not null,
    sequence    int         not null,
    created_at  timestamptz not null,
    updated_at  timestamptz not null,

    primary key (account_id, question_id),
    constraint account_question_account_id_fk foreign key (account_id) references account (id),
    constraint account_question_question_id_fk foreign key (question_id) references question (id)
);

-- create index account_question_question_id_idx on account_question (question_id);
-- create index account_question_account_id_selected_idx on account_question (account_id, selected);


create table push_token
(
    account_id uuid         not null,
    type       int          not null,
    token      varchar(500) not null,
    created_at timestamptz  not null,
    updated_at timestamptz  not null,

    primary key (account_id, type),
    constraint push_notification_account_id_fk foreign key (account_id) references account (id)
);

-- insert into push_token values ('136d4f5e-469c-4fc0-9d7d-d04c895bf99d', 0, 'testtoken', current_timestamp, current_timestamp);


create table chat
(
    id bigserial primary key
);


create table chat_message
(
    id           bigserial primary key,
    chat_id      bigint       not null,
    recipient_id uuid         not null,
    body         varchar(200) not null,
    read         boolean      not null,
    received     boolean      not null,
    created_at   timestamptz  not null,
    updated_at   timestamptz  not null,

    constraint chat_message_chat_id_fk foreign key (chat_id) references chat (id),
    constraint chat_message_recipient_id_fk foreign key (recipient_id) references account (id)
);

create index chat_message_chat_id_created_at on chat_message (chat_id, created_at);


create table sent_chat_message
(
    chat_message_id bigint primary key,
    account_id      uuid        not null,
    key             bigint      not null,
    fetched         boolean     not null,
    created_at      timestamptz not null,
    updated_at      timestamptz not null,

    constraint sent_chat_message_chat_message_id_fk foreign key (chat_message_id) references chat_message (id),
    constraint sent_chat_message_account_id_fk foreign key (account_id) references account (id)

);


create table login
(
    id         varchar(100) not null,
    type       int          not null,
    account_id uuid         not null,
    email      varchar(256),
    password   varchar(50),
    blocked    boolean      not null,

    primary key (id, type),
    constraint login_account_id_fk foreign key (account_id) references account (id)
);



-- if match between 1 and 2 then
-- 1 - 2 saved
-- 2 - 1 saved
-- application can also have match table in its light database to store messages in the chat
-- last_read_at will be updated when app is deleted
create table match
(
    version    int         not null,
    swiper_id  uuid        not null,
    swiped_id  uuid        not null,
    chat_id    bigint      not null,
    unmatched  boolean     not null,
    unmatcher  boolean     not null,
    active     boolean     not null,
    deleted    boolean     not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,

    primary key (swiper_id, swiped_id),
    constraint match_swiper_id_fk foreign key (swiper_id) references account (id),
    constraint match_swiped_id_fk foreign key (swiped_id) references account (id),
    constraint match_chat_id_fk foreign key (chat_id) references chat (id)
);

create index match_swiper_id_idx on match (swiper_id);
create index match_swiped_id_idx on match (swiped_id);
create index match_swiper_id_swiped_id_chat_id on match (swiper_id, swiped_id, chat_id);


-- TODO: refactor Photo object and table

create table photo
(
    key        varchar(40) not null,
    account_id uuid        not null,
    sequence   int         not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,

    primary key (account_id, key),
    constraint photo_account_id_fk foreign key (account_id) references account (id)
);

create index photo_account_id_idx on photo (account_id);


create table profile
(
    version             int                    not null,
    account_id          uuid primary key,
    name                varchar(15)            not null,
    birth_year          int                    not null,
    birth               Date                   not null,
    gender              boolean                not null,
    height              int,
    about               varchar(500),
    location            geography(point, 4326) not null,
    location_updated_at timestamptz            not null,
    score               int                    not null,
    page_index          int                    not null,
    enabled             boolean                not null,
    created_at          timestamptz            not null,
    updated_at          timestamptz            not null,

    constraint profile_account_id_fk foreign key (account_id) references account (id)
);


CREATE INDEX profile_location_idx ON profile USING GIST (location);


create table swipe
(
    swiper_id   uuid        not null,
    swiped_id   uuid        not null,
    clicked     boolean     not null,
    matched     boolean     not null,
    count       int         not null,
    super_click boolean     not null,
    created_at  timestamptz not null,
    updated_at  timestamptz not null,

    primary key (swiper_id, swiped_id),
    constraint swipe_swiper_id_fk foreign key (swiper_id) references account (id),
    constraint swipe_swiped_id_fk foreign key (swiped_id) references account (id)
);

create index swipe_swiper_id_idx on swipe (swiper_id);
create index swipe_swiped_id_idx on swipe (swiped_id);


create table report_reason
(
    id   serial primary key,
    name varchar(50)
);

insert into report_reason
values (default, 'inappropriate messages');
insert into report_reason
values (default, 'inappropriate photos');
insert into report_reason
values (default, 'feels like spam');
insert into report_reason
values (default, 'user is underage');
insert into report_reason
values (default, 'other');


create table report
(
    id               serial primary key,
    description      varchar(500),
    reporter_id      uuid        not null,
    reported_id      uuid        not null,
    report_reason_id int         not null,
    created_at       timestamptz not null,

    constraint report_reporter_id_fk foreign key (reporter_id) references account (id),
    constraint report_reported_id_fk foreign key (reported_id) references account (id),
    constraint report_report_reason_id_fk foreign key (report_reason_id) references report_reason (id)
);

create table setting
(
    account_id        uuid primary key,
    match_push        bool not null,
    clicked_push      bool not null,
    chat_message_push bool not null,

    constraint setting_account_id_fk foreign key (account_id) references account (id)
);


create table swipe_meta
(
    id                serial primary key,
    swipe_point       int         not null,
    super_swipe_point int         not null,
    free_swipe_period bigint      not null,
    max_free_swipe    int         not null,
    created_at        timestamptz not null,
    updated_at        timestamptz not null
);

insert into swipe_meta
values (default, 200, 300, 43200000, 10, current_timestamp, current_timestamp);



---------------------------------------------------------------------------------------------
-------------------------------------- Query Start ------------------------------------------
---------------------------------------------------------------------------------------------

select swiper_id, count(swiper_id)
from match
group by swiper_id
order by count(swiper_id) desc;

select *
from account;

select *
from login;

select *
from account_role;

select *
from role;

insert into role
values (default, 'ADMIN');
insert into role
values (default, 'USER');

insert into login
values ('807d813d-f8e6-4235-962d-f4b9cee77a52', 1, '807d813d-f8e6-4235-962d-f4b9cee77a52', '', '', false);

insert into account_role
values ('807d813d-f8e6-4235-962d-f4b9cee77a52', 3);
insert into account_role
values ('807d813d-f8e6-4235-962d-f4b9cee77a52', 4);



select *
from login;

select *
from account_role;

select *
from role;

select *
from account;


select *
from setting;

select *
from login;

insert into login
values ('default', 0, '6754cf08-0211-4470-a5bb-a9853316a3f1', 'test@naver.com', '', false, current_timestamp, current_timestamp);

update login
set type = 1;


update login
set type = 1;

select *
from account;

select *
from photo
where account_id = 'ec16330d-908f-4987-9e3f-a58b4ceebffd';

delete
from photo
where account_id = 'ec16330d-908f-4987-9e3f-a58b4ceebffd';

select *
from account;

select *
from account_question;

select a.id, p.key, min(p.sequence)
from account a
left join photo p on a.id = p.account_id
where a.id in ('4cc54bea-655d-4abc-bb03-5b7e1c3c0202', 'd4c09af6-086f-426c-b704-66ebb3ddb8a8')
group by a.id, p.key, p.sequence
order by min(p.sequence);

select *
from account;

select account_id, key, sequence
from photo
where account_id in ('4cc54bea-655d-4abc-bb03-5b7e1c3c0202', 'd4c09af6-086f-426c-b704-66ebb3ddb8a8')
order by account_id, sequence;

select *
from swipe;

-- insert into swipe values ('f0c49c14-e528-49f8-9f6f-e3cc6c0efb51', '1fe26957-f9c5-49f3-a03f-4ec36f10f0c5', true, false, 5, false, current_timestamp, current_timestamp);
insert into swipe
values ('81ae6ee5-230b-44d3-b7d5-13c3dd7dc352', '1fe26957-f9c5-49f3-a03f-4ec36f10f0c5', true, false, 5, false, current_timestamp,
        current_timestamp);


select *
from account;

update photo
set sequence = 4
where key = 'Mon May 17 17:36:48 AEST 2021';

select *
from account
where id = '89bc520c-fa42-40a4-b4f4-d50ebc3778d6';

select *
from profile
where account_id = 'bf8510c8-453d-4795-9678-be7b7fe361f1';

-- bf8510c8-453d-4795-9678-be7b7fe361f1
select *
from swipe
where swiper_id = '89bc520c-fa42-40a4-b4f4-d50ebc3778d6';


select *
from profile;


select *
from account
where id = 'fbd1b88f-1499-41f0-8d20-0c31a7d73860';

select *
from profile
where account_id = 'fbd1b88f-1499-41f0-8d20-0c31a7d73860';

select *
from swipe_meta;

select *
from wallet;

select chat_id, body, 0 as status, '2020-05-10T11:00:25.000Z' as createdAt, null as id, key
from chat_message
inner join sent_chat_message scm on chat_message.id = scm.chat_message_id
where id in (select chat_message_id from sent_chat_message);

delete
from sent_chat_message;
delete
from chat_message;


select cm.chat_id, max(id)
from match
left join chat_message cm on match.chat_id = cm.chat_id
where swiper_id = 'c2e68bd9-586b-487a-8d90-a6690516cdcd'
group by cm.chat_id;

select *
from chat_message
where id in (51, 55, 49);


select *
from account
where id = 'c2e68bd9-586b-487a-8d90-a6690516cdcd';



select *
from swipe
where (swiper_id = '039ddaa0-b861-457b-ab47-e4e3978ccc2f' and swiped_id = '5b4525ba-b325-4752-ae0e-00ece9201d3b')
   or (swiped_id = '039ddaa0-b861-457b-ab47-e4e3978ccc2f' and swiper_id = '5b4525ba-b325-4752-ae0e-00ece9201d3b');


delete
from match
where (swiper_id = '039ddaa0-b861-457b-ab47-e4e3978ccc2f' and swiped_id = '5b4525ba-b325-4752-ae0e-00ece9201d3b')
   or (swiped_id = '039ddaa0-b861-457b-ab47-e4e3978ccc2f' and swiper_id = '5b4525ba-b325-4752-ae0e-00ece9201d3b');


update swipe
set matched = false,
    clicked = false
where (swiper_id = '039ddaa0-b861-457b-ab47-e4e3978ccc2f' and swiped_id = '5b4525ba-b325-4752-ae0e-00ece9201d3b');


update swipe
set matched = false
where (swiped_id = '039ddaa0-b861-457b-ab47-e4e3978ccc2f' and swiper_id = '5b4525ba-b325-4752-ae0e-00ece9201d3b');


select *
from photo
where account_id = '1fe26957-f9c5-49f3-a03f-4ec36f10f0c5';


select *
from profile
where account_id = '1fe26957-f9c5-49f3-a03f-4ec36f10f0c5';


select *
from account_question
where account_id = '1fe26957-f9c5-49f3-a03f-4ec36f10f0c5';


---------------------------------------------------------------------------------------------
-------------------------------------- Query End --------------------------------------------
---------------------------------------------------------------------------------------------

-- ================================ ADMIN =========================================


create table admin
(
    id         serial primary key,
    name       varchar(20)  not null,
    email      varchar(100) not null,
    password   varchar(100) not null,
    account_id uuid         not null,
    created_at timestamptz  not null,
    updated_at timestamptz  not null,

    constraint admin_account_id_fk foreign key (account_id) references account (id)
);


-- blocked, fake report, etc..
create table report_resolution_type
(
    id          serial primary key,
    description varchar(20)
);



create table report_resolution
(
    id                        serial primary key,
    admin_id                  int         not null,
    report_id                 int         not null,
    report_resolution_type_id int         not null,
    created_at                timestamptz not null,

    constraint report_resolution_report_id_fk foreign key (report_id) references report (id),
    constraint report_resolution_report_resolution_type_id_fk foreign key (report_resolution_type_id) references report_resolution_type (id)
);

create table deleted_photo
(
    id         serial primary key,
    url        varchar(256) not null,
    stored_at  varchar(256) not null,
    report_id  int          not null,
    admin_id   int          not null,
    created_at timestamptz  not null,

    constraint deleted_photo_report_id_fk foreign key (report_id) references report (id)
);

create table unblock
(
    id                   serial primary key,
    description          varchar(200) not null,
    unblocked_account_id uuid         not null,
    admin_id             int          not null,
    created_at           timestamptz  not null,

    constraint unblock_unblocked_account_id_fk foreign key (unblocked_account_id) references account (id),
    constraint unblock_admin_id_fk foreign key (admin_id) references admin (id)
);


create table chat_message_back_up
(
    id         serial primary key,
    account_id uuid        not null,
    created_at timestamptz not null
);
























