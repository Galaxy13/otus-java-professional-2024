create table client
(
    id         bigserial not null primary key,
    name varchar(50)
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
