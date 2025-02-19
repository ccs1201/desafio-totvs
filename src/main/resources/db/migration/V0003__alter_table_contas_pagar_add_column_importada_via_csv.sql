alter table contaspagar.conta_pagar
    add column if not exists importada_via_csv boolean default false;

