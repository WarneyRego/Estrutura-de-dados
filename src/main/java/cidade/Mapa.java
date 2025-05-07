package cidade;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapa {
    private Grafo grafo;

    public Mapa() {
        this.grafo = new Grafo();
    }

    public void carregarMapa() {
        try {
            // Carregar o mapa a partir do arquivo JSON
            InputStream is = getClass().getClassLoader().getResourceAsStream("Dirceu__Teresina__Piauí__Brazil.json");
            if (is == null) {
                System.err.println("Arquivo JSON do mapa não encontrado. Usando mapa padrão de teste.");
                criarMapaPadrao();
                return;
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();
            
            String jsonData = jsonBuilder.toString();
            
            // Extrair nós e arestas do JSON manualmente
            Map<String, Intersecao> intersecoes = new HashMap<>();
            
            // Extrair nós
            int nodesStart = jsonData.indexOf("\"nodes\":");
            int nodesEnd = jsonData.indexOf("\"edges\":", nodesStart);
            String nodesJson = jsonData.substring(nodesStart + 8, nodesEnd);
            
            int currentPos = 0;
            while (true) {
                int idStart = nodesJson.indexOf("\"id\":", currentPos);
                if (idStart == -1) break;
                
                int idValueStart = nodesJson.indexOf("\"", idStart + 5) + 1;
                int idValueEnd = nodesJson.indexOf("\"", idValueStart);
                String id = nodesJson.substring(idValueStart, idValueEnd);
                
                int latStart = nodesJson.indexOf("\"latitude\":", idValueEnd);
                int latValueStart = latStart + 11;
                int latValueEnd = nodesJson.indexOf(",", latValueStart);
                double lat = Double.parseDouble(nodesJson.substring(latValueStart, latValueEnd).trim());
                
                int lonStart = nodesJson.indexOf("\"longitude\":", latValueEnd);
                int lonValueStart = lonStart + 12;
                int lonValueEnd = nodesJson.indexOf("}", lonValueStart);
                double lon = Double.parseDouble(nodesJson.substring(lonValueStart, lonValueEnd).trim());
                
                Intersecao intersecao = new Intersecao(id, lat, lon);
                intersecoes.put(id, intersecao);
                grafo.adicionarVertice(intersecao);
                
                currentPos = lonValueEnd;
            }
            
            // Extrair arestas
            int edgesStart = jsonData.indexOf("\"edges\":");
            int edgesEnd = jsonData.indexOf("]", edgesStart);
            String edgesJson = jsonData.substring(edgesStart + 8, edgesEnd + 1);
            
            currentPos = 0;
            while (true) {
                int idStart = edgesJson.indexOf("\"id\":", currentPos);
                if (idStart == -1) break;
                
                int idValueStart = edgesJson.indexOf("\"", idStart + 5) + 1;
                int idValueEnd = edgesJson.indexOf("\"", idValueStart);
                String id = edgesJson.substring(idValueStart, idValueEnd);
                
                int sourceStart = edgesJson.indexOf("\"source\":", idValueEnd);
                int sourceValueStart = edgesJson.indexOf("\"", sourceStart + 9) + 1;
                int sourceValueEnd = edgesJson.indexOf("\"", sourceValueStart);
                String sourceId = edgesJson.substring(sourceValueStart, sourceValueEnd);
                
                int targetStart = edgesJson.indexOf("\"target\":", sourceValueEnd);
                int targetValueStart = edgesJson.indexOf("\"", targetStart + 9) + 1;
                int targetValueEnd = edgesJson.indexOf("\"", targetValueStart);
                String targetId = edgesJson.substring(targetValueStart, targetValueEnd);
                
                int lengthStart = edgesJson.indexOf("\"length\":", targetValueEnd);
                int lengthValueStart = lengthStart + 9;
                int lengthValueEnd = edgesJson.indexOf(",", lengthValueStart);
                double length = Double.parseDouble(edgesJson.substring(lengthValueStart, lengthValueEnd).trim());
                
                int onewayStart = edgesJson.indexOf("\"oneway\":", lengthValueEnd);
                int onewayValueStart = onewayStart + 9;
                int onewayValueEnd = edgesJson.indexOf(",", onewayValueStart);
                boolean oneway = Boolean.parseBoolean(edgesJson.substring(onewayValueStart, onewayValueEnd).trim());
                
                int maxspeedStart = edgesJson.indexOf("\"maxspeed\":", onewayValueEnd);
                int maxspeedValueStart = maxspeedStart + 11;
                int maxspeedValueEnd = edgesJson.indexOf("}", maxspeedValueStart);
                double maxspeed = Double.parseDouble(edgesJson.substring(maxspeedValueStart, maxspeedValueEnd).trim());
                
                Intersecao origem = intersecoes.get(sourceId);
                Intersecao destino = intersecoes.get(targetId);
                
                if (origem != null && destino != null) {
                    grafo.adicionarAresta(new Rua(origem, destino, length, (int) maxspeed, oneway, maxspeed));
                }
                
                currentPos = maxspeedValueEnd;
            }
            
            System.out.println("Mapa da cidade de Dirceu (Teresina/PI) carregado com " + 
                            grafo.getVertices().tamanho() + " interseções e " + 
                            grafo.getArestas().tamanho() + " ruas.");
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar mapa: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Usando mapa padrão de teste como alternativa.");
            criarMapaPadrao();
        }
    }
    
    private void criarMapaPadrao() {
        // Criar mapa de teste como fallback
        // Criar interseções (nós)
        Intersecao n1 = new Intersecao("N1", 0.0, 0.0);
        Intersecao n2 = new Intersecao("N2", 1.0, 0.0);
        Intersecao n3 = new Intersecao("N3", 2.0, 0.0);
        Intersecao n4 = new Intersecao("N4", 0.0, 1.0);
        Intersecao n5 = new Intersecao("N5", 1.0, 1.0);
        Intersecao n6 = new Intersecao("N6", 2.0, 1.0);
        Intersecao n7 = new Intersecao("N7", 0.0, 2.0);
        Intersecao n8 = new Intersecao("N8", 1.0, 2.0);
        Intersecao n9 = new Intersecao("N9", 2.0, 2.0);
        
        // Adicionar nós ao grafo
        grafo.adicionarVertice(n1);
        grafo.adicionarVertice(n2);
        grafo.adicionarVertice(n3);
        grafo.adicionarVertice(n4);
        grafo.adicionarVertice(n5);
        grafo.adicionarVertice(n6);
        grafo.adicionarVertice(n7);
        grafo.adicionarVertice(n8);
        grafo.adicionarVertice(n9);
        
        // Adicionar arestas (ruas)
        // Horizontal
        grafo.adicionarAresta(new Rua(n1, n2, 100.0, 20, false, 50.0));
        grafo.adicionarAresta(new Rua(n2, n3, 100.0, 20, false, 50.0));
        grafo.adicionarAresta(new Rua(n4, n5, 100.0, 20, false, 50.0));
        grafo.adicionarAresta(new Rua(n5, n6, 100.0, 20, false, 50.0));
        grafo.adicionarAresta(new Rua(n7, n8, 100.0, 20, false, 50.0));
        grafo.adicionarAresta(new Rua(n8, n9, 100.0, 20, false, 50.0));
        
        // Vertical
        grafo.adicionarAresta(new Rua(n1, n4, 100.0, 20, false, 50.0));
        grafo.adicionarAresta(new Rua(n4, n7, 100.0, 20, false, 50.0));
        grafo.adicionarAresta(new Rua(n2, n5, 100.0, 20, false, 50.0));
        grafo.adicionarAresta(new Rua(n5, n8, 100.0, 20, false, 50.0));
        grafo.adicionarAresta(new Rua(n3, n6, 100.0, 20, false, 50.0));
        grafo.adicionarAresta(new Rua(n6, n9, 100.0, 20, false, 50.0));
        
        // Adicionar algumas ruas de mão única
        grafo.adicionarAresta(new Rua(n1, n5, 141.4, 30, true, 40.0)); // Diagonal
        grafo.adicionarAresta(new Rua(n5, n9, 141.4, 30, true, 40.0)); // Diagonal
        grafo.adicionarAresta(new Rua(n3, n5, 141.4, 30, true, 40.0)); // Diagonal
        grafo.adicionarAresta(new Rua(n5, n7, 141.4, 30, true, 40.0)); // Diagonal

        System.out.println("Mapa urbano de teste carregado com " + grafo.getVertices().tamanho() + 
                         " interseções e " + grafo.getArestas().tamanho() + " ruas.");
    }

    private Intersecao encontrarIntersecao(String id) {
        for (int i = 0; i < grafo.getVertices().tamanho(); i++) {
            Intersecao intersecao = grafo.getVertices().obter(i);
            if (intersecao.getNome().equals(id)) {
                return intersecao;
            }
        }
        return null;
    }

    public Grafo getGrafo() {
        return grafo;
    }
}