package br.com.ccs.contaspagar.domain.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CsvGenerator {

    public static void main(String[] args) {
        final int RECORD_COUNT = 50000;
        String fileName = "contas_50000.csv";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Período para geração das datas
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        Random random = new Random();

        // Arrays com as opções para descrição e situação
        String[] descriptions = {"Conta de Internet", "Aluguel", "Conta de Água", "Assinatura de Streaming"};
        String[] statuses = {"PENDENTE", "PAGA", "CANCELADA"};

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // Cabeçalho do CSV (opcional)
//            writer.println("dataVencimento,dataPagamento,valor,descricao,situacao");

            for (int i = 0; i < RECORD_COUNT; i++) {
                // Data de vencimento aleatória entre startDate e endDate
                LocalDate dataVencimento = randomDate(startDate, endDate, random);
                // Seleciona uma situação aleatória
                String situacao = statuses[random.nextInt(statuses.length)];
                
                String dataPagamento = "";
                // Para contas pagas, gera dataPagamento igual ou posterior à dataVencimento
                if ("PAGA".equals(situacao)) {
                    dataPagamento = randomDate(dataVencimento, endDate, random).format(formatter);
                }
                
                // Valor aleatório entre 50.00 e 1000.00 (arredondado para 2 casas decimais)
                double valor = 50.0 + (1000.0 - 50.0) * random.nextDouble();
                valor = Math.round(valor * 100.0) / 100.0;
                
                // Seleciona uma descrição aleatória
                String descricao = descriptions[random.nextInt(descriptions.length)];

                // Escreve a linha no arquivo CSV
                writer.printf("%s,%s,%.2f,%s,%s%n", dataVencimento.format(formatter), dataPagamento, valor, descricao, situacao);
            }
            System.out.println("CSV gerado com sucesso: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gera uma data aleatória entre duas datas (inclusive).
     */
    private static LocalDate randomDate(LocalDate startInclusive, LocalDate endInclusive, Random random) {
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endInclusive.toEpochDay();
        int days = (int) (endEpochDay - startEpochDay + 1);
        return LocalDate.ofEpochDay(startEpochDay + random.nextInt(days));
    }
}
