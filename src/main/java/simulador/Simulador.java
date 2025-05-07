package simulador;

import cidade.Dijkstra;
import cidade.Intersecao;
import cidade.Mapa;
import estruturas.FilaEncadeada;
import estruturas.ListaEncadeada;
import heuristica.HeuristicaControle;
import semaforo.ModoOperacao;
import semaforo.Semaforo;
import trafego.GeradorVeiculos;
import trafego.Veiculo;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Simulador implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient Timer timer;
    private int tempoSimulado = 0;
    private boolean pausado = false;
    private Mapa mapa;
    private GeradorVeiculos geradorVeiculos;
    private HeuristicaControle heuristica;
    private Estatisticas estatisticas;
    private ListaEncadeada<Veiculo> veiculosAtivos;
    private ModoOperacao modoOperacao;
    private static final int CAPACIDADE_MAXIMA_FILA = 10;
    
    // Controle de geração de veículos
    private boolean gerarVeiculos = true;
    private int totalVeiculosAlvo = 300; // Valor padrão
    private int veiculosGerados = 0;
    
    // Controle de taxa de geração
    private int veiculosPorCiclo = 3;
    private int frequenciaGeracao = 2;
    
    // Tempo inicial da simulação para cálculo da duração total
    private long tempoInicial;
    
    // Parâmetros de configuração dos semáforos
    private int tempoVerde = 30;     // Tempo padrão de luz verde (segundos)
    private int tempoAmarelo = 5;    // Tempo padrão de luz amarela (segundos)
    private int tempoVermelho = 30;  // Tempo padrão de luz vermelha (segundos)
    
    // Parâmetros de configuração de energia
    private double consumoDeslocamento = 0.2;      // Consumo padrão por km (unidades)
    private double consumoParada = 0.5;            // Consumo padrão por parada (unidades)
    private double multiplicadorCongestionamento = 1.5; // Multiplicador de congestionamento

    // Construtor padrão (mantido para compatibilidade)
    public Simulador() {
        this(ModoOperacao.TEMPO_ESPERA); // Usa o modo padrão
    }
    
    // Construtor que aceita o modo de operação
    public Simulador(ModoOperacao modoOperacao) {
        this(modoOperacao, 300); // Valor padrão
    }
    
    // Novo construtor que aceita o modo de operação e o total de veículos
    public Simulador(ModoOperacao modoOperacao, int totalVeiculos) {
        this.mapa = new Mapa();
        this.geradorVeiculos = new GeradorVeiculos(mapa.getGrafo());
        this.modoOperacao = modoOperacao;
        this.heuristica = new HeuristicaControle(modoOperacao);
        this.estatisticas = new Estatisticas();
        this.veiculosAtivos = new ListaEncadeada<>();
        this.tempoInicial = System.currentTimeMillis();
        this.totalVeiculosAlvo = totalVeiculos;
        
        // Ajustar taxa de geração com base no volume de tráfego
        ajustarTaxaGeracao(totalVeiculos);
    }
    
    /**
     * Define os parâmetros de configuração dos semáforos
     * @param tempoVerde tempo da luz verde em segundos
     * @param tempoAmarelo tempo da luz amarela em segundos 
     * @param tempoVermelho tempo da luz vermelha em segundos
     */
    public void setParametrosSemaforo(int tempoVerde, int tempoAmarelo, int tempoVermelho) {
        this.tempoVerde = tempoVerde;
        this.tempoAmarelo = tempoAmarelo;
        this.tempoVermelho = tempoVermelho;
    }
    
    /**
     * Define os parâmetros de consumo de energia
     * @param consumoDeslocamento consumo por km percorrido
     * @param consumoParada consumo adicional por parada
     * @param multiplicadorCongestionamento multiplicador de consumo em congestionamento
     */
    public void setParametrosEnergia(double consumoDeslocamento, double consumoParada, double multiplicadorCongestionamento) {
        this.consumoDeslocamento = consumoDeslocamento;
        this.consumoParada = consumoParada;
        this.multiplicadorCongestionamento = multiplicadorCongestionamento;
    }
    
    /**
     * Ajusta a taxa de geração de veículos com base no volume desejado
     * para distribuir melhor ao longo da simulação
     */
    private void ajustarTaxaGeracao(int totalVeiculos) {
        if (totalVeiculos <= 120) {
            // Fluxo baixo: 1 veículo a cada 3 ciclos
            veiculosPorCiclo = 1;
            frequenciaGeracao = 3;
        } else if (totalVeiculos <= 300) {
            // Fluxo médio: 3 veículos a cada 2 ciclos
            veiculosPorCiclo = 3;
            frequenciaGeracao = 2;
        } else if (totalVeiculos <= 600) {
            // Fluxo alto: 5 veículos a cada ciclo
            veiculosPorCiclo = 5;
            frequenciaGeracao = 1;
        } else {
            // Fluxo muito alto: 8 veículos a cada ciclo
            veiculosPorCiclo = 8;
            frequenciaGeracao = 1;
        }
        
        System.out.println("Taxa de geração: " + veiculosPorCiclo + " veículos a cada " + 
                         (frequenciaGeracao * 0.5) + " segundos");
    }

    public void iniciar() {
        mapa.carregarMapa();
        configurarSemaforos();
        tempoInicial = System.currentTimeMillis(); // Registra o tempo de início
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!pausado) {
                    tempoSimulado++;
                    atualizarSimulacao();
                }
            }
        }, 0, 500); // Reduzido para 500ms para acelerar a simulação
    }

    private void configurarSemaforos() {
        ListaEncadeada<Intersecao> vertices = mapa.getGrafo().getVertices();
        for (int i = 0; i < vertices.tamanho(); i++) {
            Intersecao intersecao = vertices.obter(i);
            // Configurações diferentes de acordo com o modo de operação
            switch (modoOperacao) {
                case CICLO_FIXO:
                    intersecao.setSemaforo(new Semaforo(tempoVerde, tempoAmarelo, tempoVermelho)); // Usando parâmetros configurados
                    break;
                case TEMPO_ESPERA:
                    // Para tempo de espera, ajustamos para ciclos mais curtos por padrão
                    int tempVerdeEspera = Math.min(tempoVerde, 15);
                    int tempAmareloEspera = Math.min(tempoAmarelo, 3); 
                    int tempVermelhoEspera = Math.min(tempoVermelho, 15);
                    intersecao.setSemaforo(new Semaforo(tempVerdeEspera, tempAmareloEspera, tempVermelhoEspera));
                    break;
                case CONSUMO:
                    // Para otimização de consumo, ajustamos para tempos específicos
                    int tempVerdeConsumo = (int)(tempoVerde * 0.8);
                    int tempAmareloConsumo = tempoAmarelo;
                    int tempVermelhoConsumo = (int)(tempoVermelho * 0.6);
                    intersecao.setSemaforo(new Semaforo(tempVerdeConsumo, tempAmareloConsumo, tempVermelhoConsumo));
                    break;
                default:
                    intersecao.setSemaforo(new Semaforo(tempoVerde, tempoAmarelo, tempoVermelho));
                    break;
            }
        }
        
        // Exibir informações sobre o modo selecionado
        System.out.println("Simulação iniciada com modo: " + modoOperacao);
        switch (modoOperacao) {
            case CICLO_FIXO:
                System.out.println("Os semáforos utilizarão ciclos fixos sem adaptação.");
                break;
            case TEMPO_ESPERA:
                System.out.println("Os semáforos se adaptarão para minimizar o tempo de espera dos veículos.");
                break;
            case CONSUMO:
                System.out.println("Os semáforos se adaptarão para otimizar o consumo de energia conforme horários de pico.");
                break;
        }
        
        System.out.println("Total de veículos a gerar: " + totalVeiculosAlvo);
    }

    private void atualizarSimulacao() {
        // Verificar se atingimos o limite de veículos
        if (veiculosGerados >= totalVeiculosAlvo) {
            if (gerarVeiculos) {
                System.out.println("Limite de veículos atingido (" + totalVeiculosAlvo + "). " +
                                 "Parando de gerar novos veículos.");
                gerarVeiculos = false;
            }
        }
        
        // Gerar novos veículos de acordo com a frequência definida
        if (gerarVeiculos && tempoSimulado % frequenciaGeracao == 0) {
            // Calcula quantos veículos ainda podemos criar
            int veiculosRestantes = totalVeiculosAlvo - veiculosGerados;
            int veiculosAGerar = Math.min(veiculosPorCiclo, veiculosRestantes);
            
            // Tenta gerar veículos
            for (int i = 0; i < veiculosAGerar; i++) {
                Veiculo veiculo = geradorVeiculos.gerarVeiculo();
                if (veiculo != null) {
                    FilaEncadeada<Intersecao> rota = Dijkstra.encontrarMenorCaminho(
                            mapa.getGrafo(), veiculo.getOrigem(), veiculo.getDestino());
                    veiculo.setRota(rota);
                    veiculosAtivos.adicionar(veiculo);
                    
                    // Incrementar contadores
                    veiculosGerados++;
                    estatisticas.incrementarVeiculosGerados();
                    
                    System.out.println("Veículo " + veiculo.getId() + " gerado de " +
                            veiculo.getOrigem().getNome() + " para " + veiculo.getDestino().getNome() + 
                            " (" + veiculosGerados + "/" + totalVeiculosAlvo + ")");
                    
                    // Inicializar o veículo com alguns valores para evitar tempo zero
                    if (tempoSimulado > 5) {
                        veiculo.setTempoViagem(1);
                        veiculo.setTempoEspera(1);
                    }
                }
            }
        }

        // Atualizar semáforos
        ListaEncadeada<Intersecao> vertices = mapa.getGrafo().getVertices();
        for (int i = 0; i < vertices.tamanho(); i++) {
            Intersecao intersecao = vertices.obter(i);
            heuristica.aplicar(intersecao);
        }

        // Resetar o contador de viagens simuladas antes de cada atualização
        if (veiculosAtivos.tamanho() > 0) {
            // Atualizar as estatísticas com o primeiro veículo para zerar os contadores
            estatisticas.simularViagem(veiculosAtivos.obter(0));
        }
        
        // Mover veículos - agora movemos mais rapidamente
        for (int v = 0; v < veiculosAtivos.tamanho(); v++) {
            Veiculo veiculo = veiculosAtivos.obter(v);
            // Movemos o veículo múltiplas vezes em cada atualização
            for (int m = 0; m < 3; m++) {
                moverVeiculo(veiculo);
                // Se o veículo foi removido durante o movimento, pare de movê-lo
                if (v >= veiculosAtivos.tamanho() || !veiculosAtivos.contem(veiculo)) {
                    break;
                }
            }
            
            // Registrar estatísticas parciais de viagens não concluídas
            if (v < veiculosAtivos.tamanho() && veiculosAtivos.contem(veiculo)) {
                estatisticas.simularViagem(veiculo);
            }
        }

        // Gerar relatório a cada 5 ciclos (mais frequente para vermos a mudança)
        if (tempoSimulado % 5 == 0) {
            estatisticas.gerarRelatorio();
            
            // Verificar se todos os veículos foram gerados e completaram suas viagens
            if (!gerarVeiculos && veiculosAtivos.tamanho() == 0 && veiculosGerados >= totalVeiculosAlvo) {
                System.out.println("Todos os " + totalVeiculosAlvo + " veículos completaram suas viagens!");
                finalizar();
                if (timer != null) timer.cancel();
            }
        }
    }

    private void moverVeiculo(Veiculo veiculo) {
        FilaEncadeada<Intersecao> rota = veiculo.getRota();
        if (rota.estaVazia()) {
            // Finalizando a viagem e registrando estatísticas
            // Não precisamos mais calcular aqui o consumo final, agora calculamos durante a viagem
            estatisticas.registrarViagem(veiculo);
            veiculosAtivos.remover(veiculo);
            return;
        }

        Intersecao atual = rota.consultar();
        Semaforo semaforo = atual.getSemaforo();
        
        // Comportamento diferente de acordo com o modo
        boolean podeAvancar = false;
        
        if (semaforo == null) {
            podeAvancar = true; // Sem semáforo, sempre avança
        } else if (semaforo.getEstado() == Semaforo.EstadoSemaforo.VERDE) {
            podeAvancar = true; // Semáforo verde, sempre avança
        } else {
            // Comportamento adaptado conforme o modo
            switch (modoOperacao) {
                case CICLO_FIXO:
                    // No ciclo fixo, veículos só avançam com luz verde
                    podeAvancar = false;
                    break;
                case TEMPO_ESPERA:
                    // No modo tempo de espera, permite "furar" o sinal com baixa probabilidade
                    podeAvancar = Math.random() > 0.7;
                    break;
                case CONSUMO:
                    // No modo consumo, o avanço depende do horário (simulado)
                    boolean isPico = (tempoSimulado % 100 > 70 && tempoSimulado % 100 < 85);
                    podeAvancar = isPico ? Math.random() > 0.5 : Math.random() > 0.8;
                    break;
                default:
                    podeAvancar = Math.random() > 0.3;
                    break;
            }
        }
        
        if (podeAvancar) {
            Intersecao proxima = rota.desenfileirar();
            if (!rota.estaVazia()) {
                int direcao = calcularDirecao(atual, rota.consultar());
                if (atual.getFila(direcao).tamanho() < CAPACIDADE_MAXIMA_FILA) {
                    atual.adicionarVeiculo(veiculo, direcao);
                    veiculo.setTempoViagem(veiculo.getTempoViagem() + 1);
                    
                    // Adicionar consumo por deslocamento
                    double consumoAtual = veiculo.getConsumoEnergia();
                    veiculo.setConsumoEnergia(consumoAtual + consumoDeslocamento);
                } else {
                    veiculo.setTempoEspera(veiculo.getTempoEspera() + 1);
                    
                    // Adicionar consumo por parada (congestionamento)
                    double consumoAtual = veiculo.getConsumoEnergia();
                    double consumoAdicional = consumoParada;
                    
                    // Se houver congestionamento, aplicamos o multiplicador
                    if (veiculo.getTempoEspera() > veiculo.getTempoViagem() * 0.5) {
                        consumoAdicional *= multiplicadorCongestionamento;
                    }
                    
                    veiculo.setConsumoEnergia(consumoAtual + consumoAdicional);
                }
            } else {
                // O veículo chegou ao destino
                veiculo.setTempoViagem(veiculo.getTempoViagem() + 1);
                
                // Adicionar último consumo por deslocamento
                double consumoAtual = veiculo.getConsumoEnergia();
                veiculo.setConsumoEnergia(consumoAtual + consumoDeslocamento);
                
                estatisticas.registrarViagem(veiculo);
                veiculosAtivos.remover(veiculo);
                System.out.println("Veículo " + veiculo.getId() + " chegou ao destino após " 
                    + veiculo.getTempoViagem() + " minutos de viagem e " 
                    + veiculo.getTempoEspera() + " minutos de espera. "
                    + "Consumo total: " + String.format("%.2f", veiculo.getConsumoEnergia()) + " unidades.");
            }
        } else {
            veiculo.setTempoEspera(veiculo.getTempoEspera() + 1);
            
            // Adicionar consumo por parada (semáforo)
            double consumoAtual = veiculo.getConsumoEnergia();
            double consumoAdicional = consumoParada;
            
            // Se houver congestionamento, aplicamos o multiplicador
            if (veiculo.getTempoEspera() > veiculo.getTempoViagem() * 0.5) {
                consumoAdicional *= multiplicadorCongestionamento;
            }
            
            veiculo.setConsumoEnergia(consumoAtual + consumoAdicional);
        }
    }

    private int calcularDirecao(Intersecao atual, Intersecao proxima) {
        double deltaLat = proxima.getLatitude() - atual.getLatitude();
        double deltaLon = proxima.getLongitude() - atual.getLongitude();
        if (Math.abs(deltaLat) > Math.abs(deltaLon)) {
            return deltaLat > 0 ? 0 : 2; // Norte ou Sul
        } else {
            return deltaLon > 0 ? 1 : 3; // Leste ou Oeste
        }
    }
    
    /**
     * Para a geração de novos veículos, mas continua movendo os existentes.
     */
    public void pararGeracaoVeiculos() {
        this.gerarVeiculos = false;
        System.out.println("Geração de novos veículos desativada.");
    }
    
    /**
     * Verifica se ainda existem veículos ativos na simulação.
     * @return true se ainda existem veículos em movimento, false caso contrário
     */
    public boolean possuiVeiculosAtivos() {
        return veiculosAtivos.tamanho() > 0;
    }
    
    /**
     * Retorna a quantidade de veículos ainda ativos na simulação.
     * @return o número de veículos em movimento
     */
    public int getQuantidadeVeiculosAtivos() {
        return veiculosAtivos.tamanho();
    }
    
    /**
     * Retorna o número total de veículos gerados.
     * @return o número de veículos gerados até o momento
     */
    public int getVeiculosGerados() {
        return veiculosGerados;
    }
    
    /**
     * Retorna o número total alvo de veículos.
     * @return o número total de veículos que devem ser gerados
     */
    public int getTotalVeiculosAlvo() {
        return totalVeiculosAlvo;
    }

    public void pausar() {
        pausado = true;
    }

    public void retomar() {
        pausado = false;
    }

    public void finalizar() {
        if (timer != null) {
            timer.cancel();
        }
        
        // Calcular a duração total da simulação em segundos
        long tempoFinal = System.currentTimeMillis();
        int duracaoTotal = (int) ((tempoFinal - tempoInicial) / 1000);
        
        // Gerar relatório final detalhado
        estatisticas.gerarRelatorioFinal(duracaoTotal, modoOperacao.toString());
    }

    // Métodos de serialização
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.write(tempoSimulado);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        tempoSimulado = in.readInt();
        timer = new Timer();
    }
}