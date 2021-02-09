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

                            varchar_date := concat(round((random() * (2003 - 1970)) + 1970)::varchar, '/',
                                                   round((random() * (12 - 1)) + 1)::varchar, '/',
                                                   round((random() * (27 - 1)) + 1)::varchar);

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
-- select *
-- from users
-- where st_dwithin(location, st_setsrid(st_point(126.907883, 37.463557), 4326), 5000)
--   and gender = 1
--   and (birth_year >= 1987 and birth_year <= 1990)
-- order by liked
-- limit 30
-- offset
-- 3000;


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
drop table chat_message;
drop table chat;
drop table account;


-- TODO: 2020-09-14 execute these

CREATE EXTENSION if not exists postgis;
-- create UUID extension
create extension if not exists "uuid-ossp";

-- unregister deletes account
-- liked count will be reset on every night
create table account
(
    version               int          not null,
    id                    uuid primary key default uuid_generate_v4(),
    social_login_id       varchar(100),
    identity_token        uuid,
    enabled               boolean      not null,
    blocked               boolean      not null,
    deleted               boolean      not null,
    name                  varchar(50)  not null,
    email                 varchar(256) not null,
    height                int,
    birth_year            int          not null,
    birth                 Date         not null,
    about                 varchar(500),
    gender                boolean      not null,
    score                 int          not null,
    index                 int          not null,
    point                 int          not null,
    rep_photo_key         varchar(30),
    location_updated_at   timestamptz,
    location              geography(point, 4326),
    fcm_token             varchar(200),
    account_type          int          not null,
    free_swipe            int          not null,
    free_swipe_updated_at timestamptz  not null,
    created_at            timestamptz  not null,
    updated_at            timestamptz  not null

);


CREATE INDEX account_location_idx ON account USING GIST (location);



create table photo
(
    key        varchar(30) not null,
    sequence   int         not null,
    account_id uuid        not null,

    primary key (account_id, key),
    constraint photo_account_id_fk foreign key (account_id) references account (id)
);

create index photo_account_id_idx on photo (account_id);



-- create table photo_info
-- (
--     account_id    uuid primary key not null,
--     last_sequence int              not null,
--     photo_count   int              not null,
--     created_at    timestamptz      not null,
--     updated_at    timestamptz      not null,
--
--     constraint photo_info_account_id_fk foreign key (account_id) references account (id)
-- );


create table question
(
    id            serial primary key,
    description   varchar(100) not null,
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

create table swipe
(
    swiper_id  uuid        not null,
    swiped_id  uuid        not null,
    clicked    boolean     not null,
    count      int         not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,

    primary key (swiper_id, swiped_id),
    constraint swipe_swiper_id_fk foreign key (swiper_id) references account (id),
    constraint swipe_swiped_id_fk foreign key (swiped_id) references account (id)
);

create index swipe_swiper_id_idx on swipe (swiper_id);
create index swipe_swiped_id_idx on swipe (swiped_id);

create table chat
(
    id         bigserial primary key,
    updated_at timestamptz not null
);

-- if match between 1 and 2 then
-- 1 - 2 saved
-- 2 - 1 saved
-- application can also have match table in its light database to store messages in the chat
create table match
(
    matcher_id uuid        not null,
    matched_id uuid        not null,
    chat_id    bigint      not null,
    unmatched  boolean     not null,
    unmatcher  boolean     not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,

    primary key (matcher_id, matched_id),
    constraint match_matcher_id_fk foreign key (matcher_id) references account (id),
    constraint match_matched_id_fk foreign key (matched_id) references account (id),
    constraint match_chat_id_fk foreign key (chat_id) references chat (id)
);

create index match_matcher_id_idx on match (matcher_id);
create index match_matched_id_idx on match (matched_id);
create index match_matcher_id_matched_id_chat_id on match (matcher_id, matched_id, chat_id);

create table chat_message
(
    id           bigserial primary key,
    message_id   bigint      not null,
    chat_id      bigint      not null,
    account_id   uuid        not null,
    recipient_id uuid        not null,
    body         varchar(200),
    created_at   timestamptz not null,

    constraint chat_message_chat_id_fk foreign key (chat_id) references chat (id),
    constraint chat_message_account_id_fk foreign key (account_id) references account (id),
    constraint chat_message_recipient_id_fk foreign key (recipient_id) references account (id)
);

create index chat_message_chat_id_created_at on chat_message (chat_id, created_at);


select matcher_id, count(matcher_id)
from match
group by matcher_id
order by count(matcher_id) desc;


select *
from account
where id = '6be75d61-b60a-44f9-916b-9703a9063cf5';

select *
from chat_message;

select m.updated_at, a.updated_at, c.updated_at
from match m
         left join account a on a.id = m.matcher_id
         left join chat c on m.chat_id = c.id
where matcher_id = '01ac40b1-cc3f-4a96-9663-df0ad79acee0';



select m.updated_at, a.updated_at, cm.created_at
from match m
         left join account a on m.matcher_id = a.id
         left join chat_message cm on m.matcher_id = cm.recipient_id
where matcher_id = '01ac40b1-cc3f-4a96-9663-df0ad79acee0'
  and (m.updated_at > '2021-02-05 13:37:32.196000' or
       a.updated_at > '2021-02-05 13:37:32.196000' or
       cm.created_at > '2021-02-05 13:37:32.196000');



select chat_id
from chat_message
group by chat_id;

select recipient_id, count(*)
from chat_message
group by recipient_id;


select chat_id, max(id)
from chat_message
where recipient_id = '6be75d61-b60a-44f9-916b-9703a9063cf5'
  and created_at > '2021-02-05 10:15:36.251000'
group by chat_id;

select chat_id, id
from chat_message
where recipient_id = '6be75d61-b60a-44f9-916b-9703a9063cf5'
  and created_at > '2021-02-05 10:15:36.251000'
order by chat_id, id;






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

create table report_reason
(
    id          serial primary key,
    description varchar(50)
);

create table report
(
    id               serial primary key,
    reporter_id      uuid         not null,
    reported_id      uuid         not null,
    description      varchar(200) not null,
    report_reason_id int          not null,
    created_at       timestamptz  not null,

    constraint report_reporter_id_fk foreign key (reporter_id) references account (id),
    constraint report_reported_id_fk foreign key (reported_id) references account (id),
    constraint report_report_reason_id_fk foreign key (report_reason_id) references report_reason (id)
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


select *
from account
where id = '562b9e01-611c-4e2a-b5ee-a15a0224e211';

select matched_id, count(*)
from match
group by matched_id
order by count(*) desc;

select *
from match
where matcher_id = '562b9e01-611c-4e2a-b5ee-a15a0224e211'
  and chat_id = 14;





