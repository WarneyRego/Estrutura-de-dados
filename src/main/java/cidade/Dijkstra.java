package cidade;

import estruturas.FilaEncadeada;
import estruturas.ListaEncadeada;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Dijkstra {

    public static FilaEncadeada<Intersecao> encontrarMenorCaminho(Grafo grafo, Intersecao origem, Intersecao destino) {
        // Mapa para armazenar a distância mínima para cada interseção
        Map<Intersecao, Double> distancias = new HashMap<>();
        
        // Mapa para armazenar a interseção anterior no caminho
        Map<Intersecao, Intersecao> anteriores = new HashMap<>();
        
        // Conjunto de interseções já visitadas
        Set<Intersecao> visitados = new HashSet<>();
        
        // Inicializa as distâncias com infinito e o nó origem com 0
        ListaEncadeada<Intersecao> vertices = grafo.getVertices();
        for (int i = 0; i < vertices.tamanho(); i++) {
            Intersecao intersecao = vertices.obter(i);
            distancias.put(intersecao, Double.MAX_VALUE);
        }
        distancias.put(origem, 0.0);
        
        // Enquanto houver interseções não visitadas
        while (true) {
            // Encontra a interseção não visitada com menor distância
            Intersecao atual = null;
            double menorDistancia = Double.MAX_VALUE;
            for (int i = 0; i < vertices.tamanho(); i++) {
                Intersecao intersecao = vertices.obter(i);
                if (!visitados.contains(intersecao) && distancias.get(intersecao) < menorDistancia) {
                    atual = intersecao;
                    menorDistancia = distancias.get(intersecao);
                }
            }
            
            // Se não encontrou ou chegou ao destino, sai do loop
            if (atual == null || atual.equals(destino)) {
                break;
            }
            
            // Marca como visitado
            visitados.add(atual);
            
            // Atualiza as distâncias dos vizinhos
            ListaEncadeada<Rua> arestas = grafo.obterArestasDe(atual);
            for (int i = 0; i < arestas.tamanho(); i++) {
                Rua rua = arestas.obter(i);
                Intersecao vizinho = rua.getDestino();
                
                // Ignora vizinhos já visitados
                if (visitados.contains(vizinho)) {
                    continue;
                }
                
                // Calcula nova distância possível
                double distanciaAtual = distancias.get(atual);
                double distanciaViaRua = rua.getTempoTravessia();
                double novaDistancia = distanciaAtual + distanciaViaRua;
                
                // Se a nova distância for menor, atualiza
                if (novaDistancia < distancias.get(vizinho)) {
                    distancias.put(vizinho, novaDistancia);
                    anteriores.put(vizinho, atual);
                }
            }
        }
        
        // Constrói o caminho do destino até a origem
        FilaEncadeada<Intersecao> caminho = new FilaEncadeada<>();
        Intersecao intersecao = destino;
        
        // Se não encontrou caminho
        if (!anteriores.containsKey(destino)) {
            return caminho; // Retorna fila vazia
        }
        
        // Reconstrói o caminho do destino até a origem
        while (intersecao != null) {
            caminho.enfileirar(intersecao);
            intersecao = anteriores.get(intersecao);
            
            // Para quando chegar na origem
            if (intersecao != null && intersecao.equals(origem)) {
                caminho.enfileirar(intersecao);
                break;
            }
        }
        
        // Inverte o caminho (origem a destino)
        FilaEncadeada<Intersecao> caminhoInvertido = new FilaEncadeada<>();
        while (!caminho.estaVazia()) {
            caminhoInvertido.enfileirar(caminho.desenfileirar());
        }
        
        return caminhoInvertido;
    }
} 