package cidade;

import estruturas.ListaEncadeada;

public class Grafo {
    private ListaEncadeada<Intersecao> vertices;
    private ListaEncadeada<Rua> arestas;

    public Grafo() {
        this.vertices = new ListaEncadeada<>();
        this.arestas = new ListaEncadeada<>();
    }

    public void adicionarVertice(Intersecao intersecao) {
        if (!vertices.contem(intersecao)) {
            vertices.adicionar(intersecao);
        }
    }

    public void adicionarAresta(Rua rua) {
        arestas.adicionar(rua);
        adicionarVertice(rua.getOrigem());
        adicionarVertice(rua.getDestino());
    }

    public ListaEncadeada<Rua> obterArestasDe(Intersecao v) {
        ListaEncadeada<Rua> adjacentes = new ListaEncadeada<>();
        for (int i = 0; i < arestas.tamanho(); i++) {
            Rua rua = arestas.obter(i);
            if (rua.getOrigem().equals(v)) {
                adjacentes.adicionar(rua);
            }
        }
        return adjacentes;
    }

    public ListaEncadeada<Intersecao> getVertices() {
        return vertices;
    }

    public ListaEncadeada<Rua> getArestas() {
        return arestas;
    }
}