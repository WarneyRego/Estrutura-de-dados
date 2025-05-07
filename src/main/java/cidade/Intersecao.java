package cidade;

import semaforo.Semaforo;
import trafego.Veiculo;
import estruturas.FilaEncadeada;

public class Intersecao {
    private String id;
    private double latitude;
    private double longitude;
    private Semaforo semaforo;
    private FilaEncadeada<Veiculo>[] filas;

    public Intersecao(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.semaforo = null;
        this.filas = new FilaEncadeada[4];
        for (int i = 0; i < 4; i++) {
            filas[i] = new FilaEncadeada<>();
        }
    }

    public String getNome() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setSemaforo(Semaforo semaforo) {
        this.semaforo = semaforo;
    }

    public Semaforo getSemaforo() {
        return semaforo;
    }

    public void adicionarVeiculo(Veiculo veiculo, int direcao) {
        if (direcao >= 0 && direcao < 4) {
            filas[direcao].enfileirar(veiculo);
            System.out.println("Veículo " + veiculo.getId() + " adicionado à fila da direção " + direcao + " na interseção " + id);
        } else {
            System.err.println("Direção inválida (" + direcao + ") para veículo " + veiculo.getId() + " na interseção " + id);
        }
    }

    public FilaEncadeada<Veiculo> getFila(int direcao) {
        if (direcao >= 0 && direcao < 4) {
            return filas[direcao];
        }
        return new FilaEncadeada<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Intersecao)) return false;
        Intersecao other = (Intersecao) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}