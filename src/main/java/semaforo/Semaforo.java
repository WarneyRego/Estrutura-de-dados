package semaforo;

public class Semaforo {
    private int tempoVerde;
    private int tempoAmarelo;
    private int tempoVermelho;
    private EstadoSemaforo estado;
    private int tempoAtual;
    private int cicloTotal;

    public enum EstadoSemaforo {
        VERDE, AMARELO, VERMELHO
    }

    public Semaforo(int tempoVerde, int tempoAmarelo, int tempoVermelho) {
        this.tempoVerde = tempoVerde;
        this.tempoAmarelo = tempoAmarelo;
        this.tempoVermelho = tempoVermelho;
        this.estado = EstadoSemaforo.VERDE;
        this.tempoAtual = 0;
        this.cicloTotal = tempoVerde + tempoAmarelo + tempoVermelho;
    }

    public void setTempos(int tempoVerde, int tempoAmarelo, int tempoVermelho) {
        this.tempoVerde = tempoVerde;
        this.tempoAmarelo = tempoAmarelo;
        this.tempoVermelho = tempoVermelho;
        this.cicloTotal = tempoVerde + tempoAmarelo + tempoVermelho;
    }

    public void atualizar() {
        tempoAtual = (tempoAtual + 1) % cicloTotal;
        if (tempoAtual < tempoVerde) {
            estado = EstadoSemaforo.VERDE;
        } else if (tempoAtual < tempoVerde + tempoAmarelo) {
            estado = EstadoSemaforo.AMARELO;
        } else {
            estado = EstadoSemaforo.VERMELHO;
        }
    }

    public EstadoSemaforo getEstado() {
        return estado;
    }

    public int getTempoVerde() {
        return tempoVerde;
    }
}