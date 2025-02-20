create table if not exists usuario
(
    id               uuid primary key,
    login            varchar(100)                not null,
    password         varchar(255)                not null,
    data_criacao     timestamp(6) with time zone not null,
    data_atualizacao timestamp(6) with time zone not null
);

alter table usuario
    owner to postgres;
