package semaforo;

import cidade.Intersecao;
import estruturas.FilaEncadeada;
import trafego.Veiculo;

public class ControladorSemaforos {
    private ModoOperacao modo;
    private Intersecao intersecao;

    public ControladorSemaforos(ModoOperacao modo, Intersecao intersecao) {
        this.modo = modo;
        this.intersecao = intersecao;
    }

    public void aplicarControle(Intersecao intersecao) {
        Semaforo semaforo = intersecao.getSemaforo();
        if (semaforo == null) {
            System.out.println("Interseção " + intersecao.getNome() + " não possui semáforo.");
            return;
        }

        switch (modo) {
            case CICLO_FIXO:
                semaforo.atualizar();
                break;
            case TEMPO_ESPERA:
                ajustarPorTempoEspera(semaforo);
                break;
            case CONSUMO:
                ajustarPorConsumo(semaforo);
                break;
        }
        System.out.println("Interseção " + intersecao.getNome() + ": Semáforo atualizado para " + semaforo.getEstado() + " (Verde=" + semaforo.getTempoVerde() + "s)");
    }

    private void ajustarPorTempoEspera(Semaforo semaforo) {
        int maxFila = 0;
        int totalVeiculos = 0;
        for (int i = 0; i < 4; i++) {
            FilaEncadeada<Veiculo> fila = intersecao.getFila(i);
            int tamanho = fila.tamanho();
            totalVeiculos += tamanho;
            maxFila = Math.max(maxFila, tamanho);
        }
        int novoVerde = Math.max(10, maxFila * 5);
        int novoVermelho = Math.max(10, (totalVeiculos > 0 ? (20 - maxFila) * 5 : 20));
        int novoAmarelo = 5;
        semaforo.setTempos(novoVerde, novoAmarelo, novoVermelho);
        semaforo.atualizar();
        System.out.println("Ajuste TEMPO_ESPERA: Verde=" + novoVerde + ", Vermelho=" + novoVermelho + ", TotalVeículos=" + totalVeiculos);
    }

    private void ajustarPorConsumo(Semaforo semaforo) {
        int hora = (int) (System.currentTimeMillis() % (24 * 3600 * 1000)) / (3600 * 1000);
        int totalVeiculos = 0;
        for (int i = 0; i < 4; i++) {
            totalVeiculos += intersecao.getFila(i).tamanho();
        }
        boolean isPico = (hora >= 7 && hora <= 9) || (hora >= 17 && hora <= 19);
        int novoVerde = isPico || totalVeiculos > 10 ? 40 : 20;
        int novoVermelho = isPico || totalVeiculos > 10 ? 20 : 10;
        int novoAmarelo = 5;
        semaforo.setTempos(novoVerde, novoAmarelo, novoVermelho);
        semaforo.atualizar();
        System.out.println("Ajuste CONSUMO: Verde=" + novoVerde + ", Vermelho=" + novoVermelho + ", Hora=" + hora + ", Pico=" + isPico);
    }
}