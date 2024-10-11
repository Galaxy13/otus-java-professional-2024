-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)
/*
create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

 */

-- Для @GeneratedValue(strategy = GenerationType.SEQUENCE)
create sequence client_SEQ start with 1 increment by 1;

create table client
(
    id         bigint not null primary key,
    name       varchar(50),
    address_id bigint,
);

create table address
(
    address_id bigserial not null primary key,
    street     varchar(255)
);

create table phone
(
    phone_id  bigserial not null primary key,
    number    varchar(255),
    client_id bigint    not null
)
