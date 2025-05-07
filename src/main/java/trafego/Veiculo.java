package trafego;

import cidade.Intersecao;
import estruturas.FilaEncadeada;

public class Veiculo {
    private int id;
    private Intersecao origem;
    private Intersecao destino;
    private int tempoViagem;
    private int tempoEspera;
    private FilaEncadeada<Intersecao> rota;
    private double consumoEnergia; // Consumo de energia do veículo durante a viagem

    public Veiculo(int id, Intersecao origem, Intersecao destino) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.tempoViagem = 0;
        this.tempoEspera = 0;
        this.rota = new FilaEncadeada<>();
        this.consumoEnergia = 0.0;
    }

    public int getId() {
        return id;
    }

    public Intersecao getOrigem() {
        return origem;
    }

    public Intersecao getDestino() {
        return destino;
    }

    public int getTempoViagem() {
        return tempoViagem;
    }

    public void setTempoViagem(int tempoViagem) {
        this.tempoViagem = tempoViagem;
    }

    public int getTempoEspera() {
        return tempoEspera;
    }

    public void setTempoEspera(int tempoEspera) {
        this.tempoEspera = tempoEspera;
    }

    public FilaEncadeada<Intersecao> getRota() {
        return rota;
    }

    public void setRota(FilaEncadeada<Intersecao> rota) {
        this.rota = rota;
    }
    
    /**
     * Retorna o consumo de energia do veículo
     * @return consumo em unidades de energia
     */
    public double getConsumoEnergia() {
        return consumoEnergia;
    }
    
    /**
     * Define o consumo de energia do veículo
     * @param consumoEnergia valor do consumo em unidades de energia
     */
    public void setConsumoEnergia(double consumoEnergia) {
        this.consumoEnergia = consumoEnergia;
    }
}