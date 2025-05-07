package trafego;

import cidade.Dijkstra;
import cidade.Grafo;
import cidade.Intersecao;
import estruturas.FilaEncadeada;
import java.util.Random;

public class GeradorVeiculos {
    private Grafo grafo;
    private Random random;
    private int proximoId;

    public GeradorVeiculos(Grafo grafo) {
        this.grafo = grafo;
        this.random = new Random();
        this.proximoId = 1;
    }

    public Veiculo gerarVeiculo() {
        // Selecionar origem e destino aleatórios
        int numVertices = grafo.getVertices().tamanho();
        if (numVertices < 2) {
            System.err.println("Grafo com menos de 2 vértices, impossível gerar veículo.");
            return null;
        }

        Intersecao origem = grafo.getVertices().obter(random.nextInt(numVertices));
        Intersecao destino;
        do {
            destino = grafo.getVertices().obter(random.nextInt(numVertices));
        } while (origem.equals(destino)); // Garantir que origem != destino

        // Criar veículo
        Veiculo veiculo = new Veiculo(proximoId++, origem, destino);

        // Calcular caminho com Dijkstra
        FilaEncadeada<Intersecao> caminho = Dijkstra.encontrarMenorCaminho(grafo, origem, destino);
        veiculo.setRota(caminho);

        System.out.println("Veículo " + veiculo.getId() + " gerado: " + origem.getNome() + " -> " + destino.getNome());
        return veiculo;
    }
}