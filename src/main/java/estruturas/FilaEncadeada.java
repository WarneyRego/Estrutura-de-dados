package estruturas;

public class FilaEncadeada<T> {
    private class No {
        T dado;
        No proximo;

        No(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    private No inicio;
    private No fim;
    private int tamanho;

    public FilaEncadeada() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    public void enfileirar(T elemento) {
        No novoNo = new No(elemento);
        if (inicio == null) {
            inicio = novoNo;
            fim = novoNo;
        } else {
            fim.proximo = novoNo;
            fim = novoNo;
        }
        tamanho++;
    }

    public T desenfileirar() {
        if (inicio == null) {
            throw new IllegalStateException("Fila vazia");
        }
        T dado = inicio.dado;
        inicio = inicio.proximo;
        if (inicio == null) {
            fim = null;
        }
        tamanho--;
        return dado;
    }

    public boolean estaVazia() {
        return inicio == null;
    }

    public int tamanho() {
        return tamanho;
    }

    public T consultar() {
        if (inicio == null) {
            System.out.println("Tentativa de consultar fila vazia.");
            return null; // Evitar exceção, retornar null para depuração
        }
        return inicio.dado;
    }
}