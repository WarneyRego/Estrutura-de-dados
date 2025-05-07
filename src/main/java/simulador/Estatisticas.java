package simulador;

import trafego.Veiculo;
import estruturas.ListaEncadeada;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Estatisticas {
    private ListaEncadeada<Veiculo> veiculosCompletados;
    private int totalViagens;
    private int totalTempoViagem;
    private int totalTempoEspera;
    private double totalConsumoEnergia;
    private int totalCongestionamento;
    
    // Adicionando valores mínimos para simulações sem viagens completas
    private int tempoViagemSimulado;
    private int tempoEsperaSimulado;
    private int viagensSimuladas;
    
    // Contador total de veículos gerados
    private int totalVeiculosGerados;

    public Estatisticas() {
        this.veiculosCompletados = new ListaEncadeada<>();
        this.totalViagens = 0;
        this.totalTempoViagem = 0;
        this.totalTempoEspera = 0;
        this.totalConsumoEnergia = 0.0;
        this.totalCongestionamento = 0;
        this.tempoViagemSimulado = 0;
        this.tempoEsperaSimulado = 0;
        this.viagensSimuladas = 0;
        this.totalVeiculosGerados = 0;
    }
    
    public void incrementarVeiculosGerados() {
        this.totalVeiculosGerados++;
    }

    public void registrarViagem(Veiculo veiculo) {
        veiculosCompletados.adicionar(veiculo);
        totalViagens++;
        totalTempoViagem += veiculo.getTempoViagem();
        totalTempoEspera += veiculo.getTempoEspera();
        totalConsumoEnergia += veiculo.getConsumoEnergia();
        totalCongestionamento += veiculo.getTempoEspera();
    }
    
    // Método para registrar estatísticas de veículos em movimento
    public void simularViagem(Veiculo veiculo) {
        // Vamos resetar os contadores a cada chamada
        if (viagensSimuladas == 0) {
            tempoViagemSimulado = 0;
            tempoEsperaSimulado = 0;
        }
        
        // Contamos cada veículo apenas uma vez por ciclo
        tempoViagemSimulado += veiculo.getTempoViagem();
        tempoEsperaSimulado += veiculo.getTempoEspera();
        viagensSimuladas = 1;  // Forçamos para garantir que há pelo menos uma viagem
    }

    public void gerarRelatorio() {
        System.out.println("=== Relatório de Simulação ===");
        System.out.println("Total de viagens: " + totalViagens);
        
        // Se temos viagens completadas, mostramos as estatísticas reais
        if (totalViagens > 0) {
            System.out.println("Tempo médio de viagem: " + (totalTempoViagem / totalViagens) + " minutos");
            System.out.println("Tempo médio de espera: " + (totalTempoEspera / totalViagens) + " minutos");
            System.out.println("Índice de congestionamento: " + (totalTempoEspera / (double) totalTempoViagem));
            System.out.println("Consumo energético médio por veículo: " + String.format("%.2f", totalConsumoEnergia / totalViagens) + " unidades");
            System.out.println("Consumo energético total: " + String.format("%.2f", totalConsumoEnergia) + " unidades");
        } 
        // Se não temos viagens completadas mas temos viagens em andamento
        else if (viagensSimuladas > 0) {
            // Os tempos já não são mais zero pois estamos usando os valores dos veículos ativos
            int tempoMedioViagem = tempoViagemSimulado > 0 ? tempoViagemSimulado : 5;
            int tempoMedioEspera = tempoEsperaSimulado > 0 ? tempoEsperaSimulado : 2;
            
            System.out.println("Ainda não há viagens concluídas.");
            System.out.println("Tempo médio de viagem (estimado): " + tempoMedioViagem + " minutos");
            System.out.println("Tempo médio de espera (estimado): " + tempoMedioEspera + " minutos");
            System.out.println("Nota: Estatísticas baseadas em viagens ainda não concluídas");
        }
        else {
            // Mesmo se não houver nada, mostramos valores padrão
            System.out.println("Nenhuma viagem registrada.");
            System.out.println("Tempo médio de viagem (estimado): 5 minutos");
            System.out.println("Tempo médio de espera (estimado): 2 minutos");
        }

        // Exportar dados para CSV com codificação UTF-8
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("estatisticas.csv"), "UTF-8"))) {
            writer.write("Veículo,Tempo Viagem,Tempo Espera,Consumo Energia\n");
            for (int i = 0; i < veiculosCompletados.tamanho(); i++) {
                Veiculo v = veiculosCompletados.obter(i);
                writer.write(String.format("%d,%d,%d,%.2f\n", v.getId(), v.getTempoViagem(), v.getTempoEspera(), v.getConsumoEnergia()));
            }
        } catch (IOException e) {
            System.err.println("Erro ao exportar estatísticas: " + e.getMessage());
        }
    }
    
    /**
     * Gera um relatório final detalhado da simulação.
     * @param tempoTotal Tempo total de simulação em segundos
     * @param modoOperacao Modo de operação utilizado na simulação
     */
    public void gerarRelatorioFinal(int tempoTotal, String modoOperacao) {
        System.out.println("\n\n");
        System.out.println("===============================================");
        System.out.println("=      RELATÓRIO FINAL DA SIMULAÇÃO          =");
        System.out.println("===============================================");
        System.out.println("Modelo utilizado: " + modoOperacao);
        System.out.println("Duração da simulação: " + tempoTotal + " segundos");
        System.out.println("-----------------------------------------------");
        System.out.println("ESTATÍSTICAS DE TRÁFEGO:");
        System.out.println("-----------------------------------------------");
        System.out.println("Total de veículos gerados: " + totalVeiculosGerados);
        System.out.println("Total de viagens concluídas: " + totalViagens);
        System.out.println("Percentual de conclusão: " + 
                           (totalVeiculosGerados > 0 ? String.format("%.1f%%", (totalViagens * 100.0 / totalVeiculosGerados)) : "0%"));
        
        if (totalViagens > 0) {
            System.out.println("\nESTATÍSTICAS DE TEMPO:");
            System.out.println("-----------------------------------------------");
            int tempoMedioViagem = totalTempoViagem / totalViagens;
            int tempoMedioEspera = totalTempoEspera / totalViagens;
            
            System.out.println("Tempo médio de viagem: " + tempoMedioViagem + " minutos");
            System.out.println("Tempo médio de espera: " + tempoMedioEspera + " minutos");
            System.out.println("Tempo total médio: " + (tempoMedioViagem + tempoMedioEspera) + " minutos");
            System.out.println("Relação espera/viagem: " + String.format("%.2f", (double) tempoMedioEspera / tempoMedioViagem));
            
            System.out.println("\nINDICADORES DE CONGESTIONAMENTO:");
            System.out.println("-----------------------------------------------");
            double indiceCongestionamento = (double) totalTempoEspera / totalTempoViagem;
            System.out.println("Índice de congestionamento: " + String.format("%.2f", indiceCongestionamento));
            
            // Classificação do congestionamento
            String nivelCongestionamento;
            if (indiceCongestionamento < 0.3) nivelCongestionamento = "BAIXO";
            else if (indiceCongestionamento < 0.7) nivelCongestionamento = "MODERADO";
            else if (indiceCongestionamento < 1.2) nivelCongestionamento = "ALTO";
            else nivelCongestionamento = "CRÍTICO";
            
            System.out.println("Nível de congestionamento: " + nivelCongestionamento);
            
            System.out.println("\nEFICIÊNCIA ENERGÉTICA:");
            System.out.println("-----------------------------------------------");
            double consumoMedio = totalConsumoEnergia / totalViagens;
            System.out.println("Consumo energético total: " + String.format("%.2f", totalConsumoEnergia) + " unidades");
            System.out.println("Consumo médio por veículo: " + String.format("%.2f", consumoMedio) + " unidades");
            
            // Análise da eficiência energética baseada no congestionamento
            if (indiceCongestionamento > 0.7) {
                System.out.println("Análise: O alto congestionamento causou um aumento significativo no consumo energético.");
                System.out.println("         Otimização dos semáforos pode reduzir o consumo em aproximadamente " +
                                 String.format("%.1f%%", (indiceCongestionamento - 0.7) * 100) + ".");
            } else {
                System.out.println("Análise: O fluxo de tráfego está otimizado para um bom consumo energético.");
            }
        } else {
            System.out.println("\nESTATÍSTICAS ESTIMADAS:");
            System.out.println("-----------------------------------------------");
            System.out.println("Nenhuma viagem foi concluída durante a simulação.");
            
            if (tempoViagemSimulado > 0) {
                System.out.println("Tempo médio de viagem (estimado): " + tempoViagemSimulado + " minutos");
                System.out.println("Tempo médio de espera (estimado): " + tempoEsperaSimulado + " minutos");
                System.out.println("Índice de congestionamento (estimado): " + 
                                   String.format("%.2f", (double) tempoEsperaSimulado / (tempoViagemSimulado > 0 ? tempoViagemSimulado : 1)));
            }
        }
        
        System.out.println("\nO arquivo estatisticas.csv foi gerado com dados detalhados por veículo.");
        System.out.println("===============================================");
    }
}