create table if not exists contaspagar.conta_pagar
(
    id               uuid                        not null primary key,
    data_vencimento  date                        not null,
    data_pagamento   date,
    valor            numeric(10, 2)              not null,
    descricao        varchar(255)                not null,
    situacao         varchar(255)                not null,
    data_baixa       timestamp(6) with time zone,
    data_criacao     timestamp(6) with time zone not null,
    data_atualizacao timestamp(6) with time zone not null,
    constraint conta_pagar_situacao_check
        check ((situacao)::text = ANY
               ((ARRAY ['PENDENTE'::character varying, 'PAGA'::character varying, 'CANCELADA'::character varying])::text[]))
);

alter table contaspagar.conta_pagar
    owner to postgres;

