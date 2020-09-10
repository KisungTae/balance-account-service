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
    random_gender          int;
    varchar_date  varchar(50);
    v_gender boolean;
begin

    while start_lon < end_lon
        loop
            start_lon := start_lon + 0.000300;
            while lat_count < end_lat
                loop
                    max_duplicate := round((random() * (7 - 1)) + 1);
                    while inner_count <= max_duplicate
                        loop

                            varchar_date := concat(round((random() * (2003 - 1970)) + 1970)::varchar, '/', round((random() * (12 - 1)) + 1)::varchar, '/', round((random() * (27 - 1)) + 1)::varchar);

                            random_gender := round((random() * (1 - 0)) + 0);

                            if random_gender = 1 then v_gender = false;
                            else v_gender = true;
                            end if;

                            insert into account
                            values (uuid_generate_v4(), false, 'account', 'account', to_date(varchar_date, 'YYYY/MM/DD'), 'this is about', v_gender,
                                    round((random() * (100000 - 0)) + 0), round((random() * (100 - 0)) + 0), 10, 10, current_timestamp, ST_MakePoint(start_lon, lat_count),
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



select concat(round((random() * (2003 - 1970)) + 1970)::varchar, '/', round((random() * (12 - 1)) + 1)::varchar, '/', round((random() * (27 - 1)) + 1)::varchar);



drop table unmatch;
drop table unblock;
drop table reward;
drop table reward_type;
drop table report_resolution;
drop table deleted_photo;
drop table report;
drop table report_reason;
drop table report_resolution_type;
drop table account_question;
drop table question;
drop table photo;
drop table match;
drop table favor_spent_history;
drop table favor;
drop table admin;
drop table account;
drop table account_type;

ALTER SEQUENCE question_id_seq RESTART WITH 1;
ALTER SEQUENCE account_id_seq RESTART WITH 1;
ALTER SEQUENCE account_type_id_seq RESTART WITH 1;


create extension if not exists "uuid-ossp";


-- facebook, kakao, naver, google
create table account_type
(
    id          serial primary key,
    description varchar(20)
);

-- unregister deletes account
-- favor count will be reset on every night
create table account
(
    id                     uuid primary key                default uuid_generate_v4(),
    blocked                boolean                not null,
    name                   varchar(50)            not null default '',
    email                  varchar(256) unique    not null,
    birth                  Date                   not null,
    about                  varchar(500)           not null default '',
    gender                 boolean                not null,
    score                  int                    not null,
    index                  int                    not null,
    point                  int                    not null,
    liked_count            int                    not null,
    liked_count_updated_at timestamp              not null,
    location               geography(point, 4326) not null,
    account_type_id        int                    not null,
    created_at             timestamp              not null,
    updated_at             timestamp              not null,

    constraint account_account_type_id_fk foreign key (account_type_id) references account_type (id)
);


alter table account drop column birth;
alter table account add column birth date not null default '1987-02-07';
alter table account add column enabled boolean not null default true;

alter table account rename favor_count to liked_count;
alter table account rename favor_count_updated_at to liked_count_updated_at;


CREATE INDEX account_location_idx ON account USING GIST (location);


create table photo
(
    id         serial primary key,
    sequence   int           not null,
    url        varchar(1000) not null,
    account_id uuid          not null,
    created_at timestamp     not null,
    updated_at timestamp     not null,

    constraint photo_account_id_fk foreign key (account_id) references account (id)
);


create table question
(
    id            serial primary key,
    description   varchar(100) not null,
    top_option    varchar(50)  not null,
    bottom_option varchar(50)  not null,
    created_at    timestamp    not null,
    updated_at    timestamp    not null
);

create table account_question
(
    account_id  uuid      not null,
    question_id int       not null,
    sequence    int       not null,
    selected    boolean   not null,
    created_at  timestamp not null,
    updated_at  timestamp not null,

    primary key (account_id, question_id),
    constraint account_question_account_id_fk foreign key (account_id) references account (id),
    constraint account_question_question_id_fk foreign key (question_id) references question (id)
);


-- watch ad, liked, etc...
create table reward_type
(
    id          serial primary key,
    description varchar(50) not null,
    point       int         not null
);

-- this table does not include the purchase
create table reward
(
    id             serial primary key,
    rewarded_point int       not null,
    account_id     uuid      not null,
    reward_type_id int       not null,
    created_at     timestamp not null,

    constraint reward_account_id_fk foreign key (account_id) references account (id),
    constraint reward_reward_type_id_fk foreign key (reward_type_id) references reward_type (id)
);

create table liked
(
    id         serial primary key,
    liker_id   uuid      not null,
    liked_id   uuid      not null,
    balanced   boolean   not null,
    created_at timestamp not null,

    constraint liked_liker_id_fk foreign key (liker_id) references account (id),
    constraint liked_liked_id_fk foreign key (liked_id) references account (id)
);


-- if match between 1 and 2 then
-- 1 - 2 saved
-- 2 - 1 saved
-- application can also have match table in its light database to store messages in the chat
create table match
(
    matcher_id uuid      not null,
    matched_id uuid      not null,
    unmatched  boolean   not null,
    created_at timestamp not null,

    primary key (matcher_id, matched_id),
    constraint match_matcher_id_fk foreign key (matcher_id) references account (id),
    constraint match_matched_id_fk foreign key (matched_id) references account (id)
);

create table unmatch
(
    unmatcher_id uuid      not null,
    unmatched_id uuid      not null,
    created_at   timestamp not null,

    primary key (unmatcher_id, unmatched_id),
    constraint unmatch_unmatcher_id_fk foreign key (unmatcher_id) references account (id),
    constraint unmatch_unmatched_id_fk foreign key (unmatched_id) references account (id)
);


create table admin
(
    id         serial primary key,
    name       varchar(20)  not null,
    email      varchar(100) not null,
    password   varchar(100) not null,
    account_id uuid         not null,
    created_at timestamp    not null,
    updated_at timestamp    not null,

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
    created_at       timestamp    not null,

    constraint report_reporter_id_fk foreign key (reporter_id) references account (id),
    constraint report_reported_id_fk foreign key (reported_id) references account (id),
    constraint report_report_reason_id_fk foreign key (report_reason_id) references report_reason (id)
);

create table report_resolution
(
    id                        serial primary key,
    admin_id                  int       not null,
    report_id                 int       not null,
    report_resolution_type_id int       not null,
    created_at                timestamp not null,

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
    created_at timestamp    not null,

    constraint deleted_photo_report_id_fk foreign key (report_id) references report (id)
);

create table unblock
(
    id                   serial primary key,
    description          varchar(200) not null,
    unblocked_account_id uuid         not null,
    admin_id             int          not null,
    created_at           timestamp    not null,

    constraint unblock_unblocked_account_id_fk foreign key (unblocked_account_id) references account (id),
    constraint unblock_admin_id_fk foreign key (admin_id) references admin (id)
);

create table liked_spent_history
(
    id          serial primary key,
    point_spent int       not null,
    account_id  uuid      not null,
    liked_id    int       not null,
    created_at  timestamp not null,

    constraint liked_spent_history_liked_id foreign key (liked_id) references liked (id),
    constraint liked_spent_history_account_id_fk foreign key (account_id) references account (id)
);

select *
from account;

select *
from account
where id = '9280fca6-0c69-4c5a-a890-bd9d0b4079f6';



insert into match values ('3cadd50a-2574-4625-811b-6e0c1e3cb38d', 'b94aef4f-3d7e-424c-8945-e57bad465212', false, current_timestamp);
insert into match values ('3cadd50a-2574-4625-811b-6e0c1e3cb38d', 'c2d34ab2-9b19-4502-b3d8-45d3eba15d14', false, current_timestamp);
insert into match values ('3cadd50a-2574-4625-811b-6e0c1e3cb38d', 'e66e0b09-eee4-4700-b9d1-d6515d7852c5', false, current_timestamp);


select *
from match;


select *
from question;

select *
from account_question;

select *
from account_question;

insert into account_question
values ('10144511-b780-49e3-805a-51ca29d1240a', 2, 1, true, true, current_timestamp, current_timestamp);
insert into account_question
values ('10144511-b780-49e3-805a-51ca29d1240a', 3, 2, true, true, current_timestamp, current_timestamp);



select *
from account a
where a.id not in (select m.matched_id from match m where matcher_id = '3cadd50a-2574-4625-811b-6e0c1e3cb38d')
and st_dwithin(location, st_setsrid(st_point(126.807883, 32.463557), 4326), 5000);


select *
from photo;

select name, index, point, birth
from account
order by point;


select *
from account_question;

delete from match where matched_id is not null;

select *
from match where matcher_id = 'c7a30bae-795f-47a0-88ad-69416f5b323e';


delete from photo where id is not null;
delete from match where matched_id is not null;
delete from account_question where account_id is not null;
delete from account where id is not null;



-- select *
-- from account
-- where st_dwithin(location, st_setsrid(st_point(126.907883, 37.463557), 4326), 5000)
--   and gender = 1
--   and (birth_year >= 1987 and birth_year <= 1990)
-- order by liked
-- limit 30 offset 3000;