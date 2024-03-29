
-- pushSetting: distance, age, gender will be included in query from front end, dont have to create a table for settings
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
drop table account_role;
-- drop table pushSetting;
drop table push_setting;
drop table refresh_token;
drop table account;

select *
from push_token;


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

create table refresh_token
(
    account_id uuid primary key,
    key        uuid        not null,
    updated_at timestamptz not null,

    constraint refresh_token_account_id_fk foreign key (account_id) references account (id)
);


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
    login      boolean      not null,
    token      varchar(500) not null,
    created_at timestamptz  not null,
    updated_at timestamptz  not null,

    primary key (account_id, type),
    constraint push_notification_account_id_fk foreign key (account_id) references account (id)
);

create index push_token_token_idx on push_token (token);


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

create table push_setting
(
    account_id        uuid primary key,
    match_push        bool not null,
    clicked_push      bool not null,
    chat_message_push bool not null,
    email_push        bool not null,

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



create table role
(
    id   serial primary key,
    name varchar(30) not null
);

insert into role
values (default, 'ADMIN');
-- insert into role
-- values (default, 'USER');

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

---------------------------------------------------------------------------------------------
-------------------------------------- Query Start ------------------------------------------
---------------------------------------------------------------------------------------------



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
























