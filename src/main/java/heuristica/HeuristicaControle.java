package heuristica;

import semaforo.ControladorSemaforos;
import semaforo.ModoOperacao;
import cidade.Intersecao;

public class HeuristicaControle {
    private ModoOperacao modo;

    public HeuristicaControle(ModoOperacao modo) {
        this.modo = modo;
    }

    public void aplicar(Intersecao intersecao) {
        ControladorSemaforos controlador = new ControladorSemaforos(modo, intersecao);
        controlador.aplicarControle(intersecao);
    }
}