package estruturas;

public class ListaEncadeada<T> {
    private class No {
        T dado;
        No proximo;

        No(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    private No inicio;
    private int tamanho;

    public ListaEncadeada() {
        this.inicio = null;
        this.tamanho = 0;
    }

    public void adicionar(T elemento) {
        No novoNo = new No(elemento);
        if (inicio == null) {
            inicio = novoNo;
        } else {
            No atual = inicio;
            while (atual.proximo != null) {
                atual = atual.proximo;
            }
            atual.proximo = novoNo;
        }
        tamanho++;
    }

    public T obter(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException();
        }
        No atual = inicio;
        for (int i = 0; i < indice; i++) {
            atual = atual.proximo;
        }
        return atual.dado;
    }

    public void remover(T elemento) {
        No atual = inicio;
        No anterior = null;
        while (atual != null) {
            if (atual.dado.equals(elemento)) {
                if (anterior == null) {
                    inicio = atual.proximo;
                } else {
                    anterior.proximo = atual.proximo;
                }
                tamanho--;
                return;
            }
            anterior = atual;
            atual = atual.proximo;
        }
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean contem(T elemento) {
        No atual = inicio;
        while (atual != null) {
            if (atual.dado.equals(elemento)) {
                return true;
            }
            atual = atual.proximo;
        }
        return false;
    }
}