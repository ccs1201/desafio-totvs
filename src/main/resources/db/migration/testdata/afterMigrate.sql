delete from conta_pagar;

INSERT INTO conta_pagar (
    id,
    data_vencimento,
    data_pagamento,
    data_baixa,
    valor,
    descricao,
    situacao,
    data_criacao,
    data_atualizacao
) VALUES
      (
          gen_random_uuid(),
          '2024-02-01',  -- data_vencimento
          NULL,          -- data_pagamento (unpaid)
          NULL,          -- data_baixa
          1250.00,       -- valor
          'Aluguel Comercial Janeiro',  -- descricao
          'PENDENTE',    -- situacao
          CURRENT_TIMESTAMP,  -- data_criacao
          CURRENT_TIMESTAMP  -- data_atualizacao
      ),
      (
          gen_random_uuid(),
          '2024-01-15',  -- data_vencimento
          '2024-01-15',  -- data_pagamento
          CURRENT_TIMESTAMP,  -- data_baixa
          350.50,        -- valor
          'Conta de Energia',  -- descricao
          'PAGA',        -- situacao
          CURRENT_TIMESTAMP,  -- data_criacao
          CURRENT_TIMESTAMP  -- data_atualizacao
      ),
      (
          gen_random_uuid(),
          '2024-01-20',  -- data_vencimento
          NULL,          -- data_pagamento
          CURRENT_TIMESTAMP,  -- data_baixa
          180.00,        -- valor
          'Internet Empresarial',  -- descricao
          'CANCELADA',   -- situacao
          CURRENT_TIMESTAMP,  -- data_criacao
          CURRENT_TIMESTAMP  -- data_atualizacao
      ),
      (
          gen_random_uuid(),
          '2024-02-05',  -- data_vencimento
          NULL,          -- data_pagamento
          NULL,          -- data_baixa
          2500.00,       -- valor
          'Fornecedor ABC Ltda',  -- descricao
          'PENDENTE',    -- situacao
          CURRENT_TIMESTAMP,  -- data_criacao
          CURRENT_TIMESTAMP  -- data_atualizacao
      ),
      (
          gen_random_uuid(),
          '2024-01-10',  -- data_vencimento
          '2024-01-10',  -- data_pagamento
          CURRENT_TIMESTAMP,  -- data_baixa
          750.25,        -- valor
          'Material de Escritório',  -- descricao
          'PAGA',        -- situacao
          CURRENT_TIMESTAMP,  -- data_criacao
          CURRENT_TIMESTAMP  -- data_atualizacao
      );

