import simulador.Simulador;
import semaforo.ModoOperacao;
import semaforo.Semaforo;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== SIMULADOR DE TRÁFEGO URBANO ===");
        
        // ---- CONFIGURAÇÃO DO MODO DE OPERAÇÃO DOS SEMÁFOROS ----
        
        System.out.println("\n=== CONFIGURAÇÃO DO MODO DE OPERAÇÃO ===");
        System.out.println("Selecione o modelo de operação dos semáforos:");
        System.out.println("1 - Ciclo fixo (sem heurísticas)");
        System.out.println("2 - Otimização do tempo de espera (ajuste dinâmico conforme filas)");
        System.out.println("3 - Otimização do consumo de energia (ajuste conforme períodos de pico)");
        System.out.print("Opção: ");
        
        int opcaoModelo = 1;
        try {
            opcaoModelo = Integer.parseInt(scanner.nextLine());
            if (opcaoModelo < 1 || opcaoModelo > 3) {
                System.out.println("Opção inválida. Usando modelo padrão (Ciclo fixo).");
                opcaoModelo = 1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Usando modelo padrão (Ciclo fixo).");
        }
        
        // Conversão da opção para o modo de operação correspondente
        ModoOperacao modoOperacao;
        switch (opcaoModelo) {
            case 2:
                modoOperacao = ModoOperacao.TEMPO_ESPERA;
                System.out.println("Modelo selecionado: Otimização do tempo de espera");
                break;
            case 3:
                modoOperacao = ModoOperacao.CONSUMO;
                System.out.println("Modelo selecionado: Otimização do consumo de energia");
                break;
            default:
                modoOperacao = ModoOperacao.CICLO_FIXO;
                System.out.println("Modelo selecionado: Ciclo fixo");
                break;
        }
        
        // ---- CONFIGURAÇÃO DOS TEMPOS DOS SEMÁFOROS ----
        
        System.out.println("\n=== CONFIGURAÇÃO DOS TEMPOS DOS SEMÁFOROS ===");
        System.out.println("Deseja personalizar os tempos dos semáforos? (S/N): ");
        String respTemposSemaforo = scanner.nextLine();
        
        int tempoVerde = 30;    // tempo padrão para luz verde (segundos)
        int tempoAmarelo = 5;   // tempo padrão para luz amarela (segundos)
        int tempoVermelho = 30; // tempo padrão para luz vermelha (segundos)
        
        if (respTemposSemaforo.equalsIgnoreCase("S")) {
            try {
                System.out.print("Tempo de luz VERDE (em segundos, padrão 30): ");
                int inputVerde = Integer.parseInt(scanner.nextLine());
                tempoVerde = inputVerde > 0 ? inputVerde : 30;
                
                System.out.print("Tempo de luz AMARELA (em segundos, padrão 5): ");
                int inputAmarelo = Integer.parseInt(scanner.nextLine());
                tempoAmarelo = inputAmarelo > 0 ? inputAmarelo : 5;
                
                System.out.print("Tempo de luz VERMELHA (em segundos, padrão 30): ");
                int inputVermelho = Integer.parseInt(scanner.nextLine());
                tempoVermelho = inputVermelho > 0 ? inputVermelho : 30;
                
                System.out.println("Configuração dos semáforos personalizada aplicada.");
            } catch (NumberFormatException e) {
                System.out.println("Valores inválidos. Usando configuração padrão para os semáforos.");
            }
        } else {
            System.out.println("Usando configuração padrão para os semáforos.");
        }
        
        // ---- CONFIGURAÇÃO DOS PARÂMETROS DE CONSUMO DE ENERGIA ----
        
        System.out.println("\n=== CONFIGURAÇÃO DOS PARÂMETROS DE ENERGIA ===");
        System.out.println("Deseja personalizar os parâmetros de consumo de energia? (S/N): ");
        String respConsumo = scanner.nextLine();
        
        double consumoDeslocamento = 0.2;      // consumo padrão por km percorrido (unidades/km)
        double consumoParada = 0.5;            // consumo adicional padrão por parada (unidades)
        double multiplicadorCongestionamento = 1.5; // multiplicador padrão de congestionamento
        
        if (respConsumo.equalsIgnoreCase("S")) {
            try {
                System.out.print("Consumo por deslocamento (unidades por km, padrão 0.2): ");
                double inputDeslocamento = Double.parseDouble(scanner.nextLine());
                consumoDeslocamento = inputDeslocamento > 0 ? inputDeslocamento : 0.2;
                
                System.out.print("Consumo por parada em semáforo (unidades por parada, padrão 0.5): ");
                double inputParada = Double.parseDouble(scanner.nextLine());
                consumoParada = inputParada > 0 ? inputParada : 0.5;
                
                System.out.print("Consumo adicional em congestionamento (multiplicador, padrão 1.5): ");
                double inputCongestionamento = Double.parseDouble(scanner.nextLine());
                multiplicadorCongestionamento = inputCongestionamento > 1 ? inputCongestionamento : 1.5;
                
                System.out.println("Configuração de consumo de energia personalizada aplicada.");
            } catch (NumberFormatException e) {
                System.out.println("Valores inválidos. Usando configuração padrão para consumo de energia.");
            }
        } else {
            System.out.println("Usando configuração padrão para consumo de energia.");
        }
        
        // ---- SELEÇÃO DO NÍVEL DE FLUXO DE VEÍCULOS ----
        
        System.out.println("\n=== CONFIGURAÇÃO DO FLUXO DE VEÍCULOS ===");
        System.out.println("Selecione o nível de fluxo de veículos:");
        System.out.println("1 - Fluxo baixo (120 veículos)");
        System.out.println("2 - Fluxo médio (300 veículos)");
        System.out.println("3 - Fluxo alto (600 veículos)");
        System.out.println("4 - Personalizado (você define o número)");
        System.out.print("Opção: ");
        
        int opcaoFluxo = 2; // Padrão: fluxo médio
        int totalVeiculos = 300; // Valor padrão
        
        try {
            opcaoFluxo = Integer.parseInt(scanner.nextLine());
            switch (opcaoFluxo) {
                case 1:
                    totalVeiculos = 120; // Fluxo baixo
                    System.out.println("Fluxo selecionado: Baixo (120 veículos)");
                    break;
                case 2:
                    totalVeiculos = 300; // Fluxo médio
                    System.out.println("Fluxo selecionado: Médio (300 veículos)");
                    break;
                case 3:
                    totalVeiculos = 600; // Fluxo alto
                    System.out.println("Fluxo selecionado: Alto (600 veículos)");
                    break;
                case 4:
                    // Fluxo personalizado
                    System.out.print("Digite o número total de veículos desejado: ");
                    try {
                        totalVeiculos = Integer.parseInt(scanner.nextLine());
                        if (totalVeiculos <= 0) {
                            System.out.println("Valor inválido. Usando fluxo médio (300 veículos).");
                            totalVeiculos = 300;
                        } else {
                            System.out.println("Fluxo personalizado: " + totalVeiculos + " veículos");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inválida. Usando fluxo médio (300 veículos).");
                        totalVeiculos = 300;
                    }
                    break;
                default:
                    System.out.println("Opção inválida. Usando fluxo médio (300 veículos).");
                    totalVeiculos = 300;
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Usando fluxo médio (300 veículos).");
        }
        
        // ---- SELEÇÃO DO TEMPO DE SIMULAÇÃO ----
        
        System.out.println("\n=== CONFIGURAÇÃO DO TEMPO DE SIMULAÇÃO ===");
        System.out.print("Digite o tempo de simulação em segundos (0 para executar indefinidamente): ");
        int tempoSimulacao = 0;
        try {
            tempoSimulacao = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Usando tempo indefinido.");
        }
        
        // Resumo das configurações selecionadas
        System.out.println("\n=== RESUMO DAS CONFIGURAÇÕES ===");
        System.out.println("Modo de operação: " + modoOperacao);
        System.out.println("Tempos do semáforo: Verde=" + tempoVerde + "s, Amarelo=" + 
                         tempoAmarelo + "s, Vermelho=" + tempoVermelho + "s");
        System.out.println("Consumo de energia: Deslocamento=" + consumoDeslocamento + 
                         ", Parada=" + consumoParada + 
                         ", Multiplicador de congestionamento=" + multiplicadorCongestionamento);
        System.out.println("Total de veículos: " + totalVeiculos);
        System.out.println("Tempo de simulação: " + (tempoSimulacao > 0 ? tempoSimulacao + "s" : "Indefinido"));
        
        // Confirmação para iniciar a simulação
        System.out.println("\nPressione ENTER para iniciar a simulação...");
        scanner.nextLine();
        
        // Inicialização do simulador com os parâmetros configurados
        Simulador simulador = new Simulador(modoOperacao, totalVeiculos);
        
        // Configurar parâmetros do simulador
        simulador.setParametrosSemaforo(tempoVerde, tempoAmarelo, tempoVermelho);
        simulador.setParametrosEnergia(consumoDeslocamento, consumoParada, multiplicadorCongestionamento);
        
        simulador.iniciar();
        
        if (tempoSimulacao > 0) {
            try {
                System.out.println("Simulação em andamento por " + tempoSimulacao + " segundos...");
                System.out.println("Após esse tempo, aguardaremos todos os veículos concluírem suas viagens.");
                
                // Esperar até o fim do tempo de simulação
                TimeUnit.SECONDS.sleep(tempoSimulacao);
                
                // Parar de gerar novos veículos, mas continuar a simulação
                System.out.println("\nTempo definido atingido. Parando de gerar novos veículos...");
                simulador.pararGeracaoVeiculos();
                
                // Esperar até que todos os veículos concluam suas viagens
                while (simulador.possuiVeiculosAtivos()) {
                    System.out.println("Aguardando conclusão de todas as viagens... " + 
                                      simulador.getQuantidadeVeiculosAtivos() + " veículos restantes.");
                    TimeUnit.SECONDS.sleep(2);
                }
                
                // Finalizar simulação e mostrar relatório final
                System.out.println("\nTodas as viagens foram concluídas! Gerando relatório final...");
                simulador.finalizar();
                
            } catch (InterruptedException e) {
                System.err.println("Simulação interrompida: " + e.getMessage());
                simulador.finalizar(); // Garantir que o relatório seja gerado mesmo se interrompido
            }
        } else {
            System.out.println("Simulação em andamento... Pressione CTRL+C para finalizar e ver o relatório final.");
            System.out.println("Quando finalizar, todos os veículos ativos concluirão suas viagens antes do relatório final.");
            
            // Registra um gancho para finalizar a simulação ao encerrar o programa
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        System.out.println("\nParando de gerar novos veículos...");
                        simulador.pararGeracaoVeiculos();
                        
                        // Esperar até que todos os veículos concluam suas viagens
                        while (simulador.possuiVeiculosAtivos()) {
                            System.out.println("Aguardando conclusão de todas as viagens... " + 
                                              simulador.getQuantidadeVeiculosAtivos() + " veículos restantes.");
                            TimeUnit.SECONDS.sleep(2);
                        }
                        
                        System.out.println("\nTodas as viagens foram concluídas! Gerando relatório final...");
                        simulador.finalizar();
                        
                        // Dar tempo para o relatório final ser exibido antes de encerrar
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        System.err.println("Erro ao finalizar simulação: " + e.getMessage());
                    }
                }
            });
        }
    }
}