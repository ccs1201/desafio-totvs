delete
from conta_pagar;

INSERT INTO conta_pagar (id,
                         data_vencimento,
                         data_pagamento,
                         data_baixa,
                         valor,
                         descricao,
                         situacao,
                         data_criacao,
                         data_atualizacao)
VALUES ('31dadf3b-7974-4e1a-880a-dea78cfaeb10',
        '2025-02-01', -- data_vencimento
        NULL, -- data_pagamento (unpaid)
        NULL, -- data_baixa
        1250.00, -- valor
        'Aluguel Comercial Janeiro', -- descricao
        'PENDENTE', -- situacao
        CURRENT_TIMESTAMP, -- data_criacao
        CURRENT_TIMESTAMP -- data_atualizacao
       ),
       ('f5d9a875-5a8c-4147-9a89-31c181f48073',
        '2025-01-15', -- data_vencimento
        '2025-01-15', -- data_pagamento
        CURRENT_TIMESTAMP, -- data_baixa
        350.50, -- valor
        'Conta de Energia', -- descricao
        'PAGA', -- situacao
        CURRENT_TIMESTAMP, -- data_criacao
        CURRENT_TIMESTAMP -- data_atualizacao
       ),
       ('c6072644-5057-42cb-b977-ff42d6601751',
        '2025-01-20', -- data_vencimento
        NULL, -- data_pagamento
        CURRENT_TIMESTAMP, -- data_baixa
        180.00, -- valor
        'Internet Empresarial', -- descricao
        'CANCELADA', -- situacao
        CURRENT_TIMESTAMP, -- data_criacao
        CURRENT_TIMESTAMP -- data_atualizacao
       ),
       ('d0da4f97-8917-4f52-aaf6-0741eb72da01',
        '2025-02-05', -- data_vencimento
        NULL, -- data_pagamento
        NULL, -- data_baixa
        2500.00, -- valor
        'Fornecedor ABC Ltda', -- descricao
        'PENDENTE', -- situacao
        CURRENT_TIMESTAMP, -- data_criacao
        CURRENT_TIMESTAMP -- data_atualizacao
       ),
       ('0695f12f-39fc-4b85-abd3-06ae41c27ccf',
        '2025-01-10', -- data_vencimento
        '2025-01-10', -- data_pagamento
        CURRENT_TIMESTAMP, -- data_baixa
        750.25, -- valor
        'Material de Escritório', -- descricao
        'PAGA', -- situacao
        CURRENT_TIMESTAMP, -- data_criacao
        CURRENT_TIMESTAMP -- data_atualizacao
       );

