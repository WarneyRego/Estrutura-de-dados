package cidade;

public class Rua {
    private Intersecao origem;
    private Intersecao destino;
    private double comprimento;
    private int tempoTravessia;
    private boolean sentidoUnico;
    private double velocidadeMaxima;

    public Rua(Intersecao origem, Intersecao destino, double comprimento, int tempoTravessia, boolean sentidoUnico, double velocidadeMaxima) {
        this.origem = origem;
        this.destino = destino;
        this.comprimento = comprimento;
        this.tempoTravessia = tempoTravessia;
        this.sentidoUnico = sentidoUnico;
        this.velocidadeMaxima = velocidadeMaxima;
    }

    public Intersecao getOrigem() {
        return origem;
    }

    public Intersecao getDestino() {
        return destino;
    }

    public int getTempoTravessia() {
        return tempoTravessia;
    }

    public double getComprimento() {
        return comprimento;
    }

    public boolean isSentidoUnico() {
        return sentidoUnico;
    }

    public double getVelocidadeMaxima() {
        return velocidadeMaxima;
    }
}