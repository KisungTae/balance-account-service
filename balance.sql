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



drop table profile;

-- TODO: 2020-09-14 execute these

CREATE EXTENSION if not exists postgis;
-- create UUID extension
create extension if not exists "uuid-ossp";


create table login
(
    id         varchar(100) not null,
    type       int          not null,
    account_id uuid         not null,
    email      varchar(256),
    password   varchar(50)  not null,
    blocked    boolean      not null,
    created_at timestamptz  not null,

    primary key (id, type),
    constraint social_login_account_id_fk foreign key (account_id) references account (id)
);

-- unregister deletes account
-- liked count will be reset on every night
create table account
(
    id                    uuid primary key,
    identity_token        uuid        not null,
    name                  varchar(15) not null,
    point                 int         not null,
    rep_photo_key         varchar(30) not null,
    free_swipe            int         not null,
    free_swipe_updated_at timestamptz not null,
    blocked               boolean     not null,
    deleted               boolean     not null,
    version               int         not null,
    created_at            timestamptz not null,
    updated_at            timestamptz not null
);


create table push_token
(
    account_id uuid         not null,
    type       int          not null,
    key        varchar(500) not null,
    created_at timestamptz  not null,
    updated_at timestamptz  not null,

    primary key (account_id, type),
    constraint push_notification_account_id_fk foreign key (account_id) references account (id)
);


create table profile
(
    account_id          uuid primary key,
    identity_token      uuid                   not null,
    name                varchar(15)            not null,
    birth_year          int                    not null,
    birth               Date                   not null,
    gender              boolean                not null,
    height              int,
    about               varchar(500)           not null,
    location            geography(point, 4326) not null,
    location_updated_at timestamptz            not null,
    score               int                    not null,
    page_index          int                    not null,
    enabled             boolean                not null,
    deleted             boolean                not null,
    version             int                    not null,
    created_at          timestamptz            not null,
    updated_at          timestamptz            not null,

    constraint profile_account_id_fk foreign key (account_id) references account (id)
);

CREATE INDEX profile_location_idx ON profile USING GIST (location);



create table photo
(
    key        varchar(30) not null,
    sequence   int         not null,
    account_id uuid        not null,

    primary key (account_id, key),
    constraint photo_account_id_fk foreign key (account_id) references account (id)
);

create index photo_account_id_idx on photo (account_id);


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

create table swipe
(
    version     int         not null,
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

create table chat
(
    id bigserial primary key
);

-- if match between 1 and 2 then
-- 1 - 2 saved
-- 2 - 1 saved
-- application can also have match table in its light database to store messages in the chat
-- last_read_at will be updated when app is deleted
create table match
(
    version    int         not null,
    matcher_id uuid        not null,
    matched_id uuid        not null,
    chat_id    bigint      not null,
    unmatched  boolean     not null,
    unmatcher  boolean     not null,
    active     boolean     not null,
    deleted    boolean     not null,
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
    message_id   bigint       not null,
    chat_id      bigint       not null,
    account_id   uuid         not null,
    recipient_id uuid         not null,
    body         varchar(200) not null,
    read         boolean      not null,
    fetched      boolean      not null,
    received     boolean      not null,
    created_at   timestamptz  not null,

    constraint chat_message_chat_id_fk foreign key (chat_id) references chat (id),
    constraint chat_message_account_id_fk foreign key (account_id) references account (id),
    constraint chat_message_recipient_id_fk foreign key (recipient_id) references account (id)
);

create index chat_message_chat_id_created_at on chat_message (chat_id, created_at);



---------------------------------------------------------------------------------------------
-------------------------------------- Query Start ------------------------------------------
---------------------------------------------------------------------------------------------

alter table account rename account_type to login_type;


select *
from push_token;

select *
from account;

explain analyse
select *
from question
where id in (select question_id
             from account_question
             where account_id = '8d087dc8-2dc3-40d4-b0ed-f3e509c23d2a' and selected = true);


explain analyse
select *
from question q
left join account_question aq on q.id = aq.question_id
where aq.account_id = '8d087dc8-2dc3-40d4-b0ed-f3e509c23d2a'
  and aq.selected = true;


select *
from account_question;

select *
from account_question
where account_id = '8d087dc8-2dc3-40d4-b0ed-f3e509c23d2a';

insert into account_question
values ('8d087dc8-2dc3-40d4-b0ed-f3e509c23d2a', 24, false, true, 4, current_timestamp, current_timestamp);



explain
select *
from account a
left join account_question aq on a.id = aq.account_id
left join question q on aq.question_id = q.id
where a.id = '67cfc7e3-9302-4a98-8870-b113baf81d73'
  and aq.selected = true;


select s1.swiper_id, s1.swiped_id, s2.swiper_id, s2.swiped_id
from swipe s1
inner join swipe s2 on s1.swiper_id = s2.swiped_id and s1.swiped_id = s2.swiper_id;

update swipe
set clicked = false,
    matched = false
where swiper_id is not null;
delete
from match
where matcher_id is not null;

select *
from account
where id = '67cfc7e3-9302-4a98-8870-b113baf81d73'
   or id = 'f8849505-878d-4d8a-8632-e284a1010529';



select *
from swipe
where (swiper_id = '67cfc7e3-9302-4a98-8870-b113baf81d73' and swiped_id = 'f8849505-878d-4d8a-8632-e284a1010529')
   or (swiper_id = 'f8849505-878d-4d8a-8632-e284a1010529' and swiped_id = '67cfc7e3-9302-4a98-8870-b113baf81d73');

select *
from swipe
where (swiper_id = '8d087dc8-2dc3-40d4-b0ed-f3e509c23d2a' and swiped_id = 'e01794ae-cb65-462e-9a07-6a4498b6ecfe')
   or (swiper_id = 'e01794ae-cb65-462e-9a07-6a4498b6ecfe' and swiped_id = '8d087dc8-2dc3-40d4-b0ed-f3e509c23d2a');



select *
from swipe
where swiper_id = 'e20b05c2-56b6-4791-84c1-d8af6d82a9e9'
  and swiped_id = 'de2198e4-4d49-45bb-9ae8-c094e08a60e0';

select *
from chat_message
order by id;

select *
from account
where id = 'f77865c1-e370-48f0-ad6a-c64154c938a0';


select matcher_id, count(matcher_id)
from match
group by matcher_id
order by count(matcher_id) desc;


select *
from match
where matcher_id = 'f77865c1-e370-48f0-ad6a-c64154c938a0';

select *
from swipe;

select *
from chat_message;

delete
from chat_message;

select *
from chat_message
where account_id = '1e3a289c-9419-409e-9432-cd989b2547fc';



update chat_message
set fetched  = false,
    received = false
where id is not null;

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


create table chat_message_back_up
(
    id         serial primary key,
    account_id uuid        not null,
    created_at timestamptz not null
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





