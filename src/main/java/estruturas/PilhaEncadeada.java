package estruturas;

public class PilhaEncadeada<T> {
    private class No {
        T dado;
        No proximo;

        No(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    private No topo;

    public PilhaEncadeada() {
        this.topo = null;
    }

    public void empilhar(T elemento) {
        No novoNo = new No(elemento);
        novoNo.proximo = topo;
        topo = novoNo;
    }

    public T desempilhar() {
        if (topo == null) {
            throw new IllegalStateException("Pilha vazia");
        }
        T dado = topo.dado;
        topo = topo.proximo;
        return dado;
    }

    public boolean estaVazia() {
        return topo == null;
    }
}