// O. Bittel;
// 2.8.2023

package directedGraph;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;


/**
 * Klasse zur Analyse von Web-Sites.
 *
 * @author Oliver Bittel
 * @since 30.10.2023
 */
public class AnalyzeWebSite {
    public static void main(String[] args) throws IOException {
        // Graph aus Website erstellen und ausgeben:
        //DirectedGraph<String> webSiteGraph = buildGraphFromWebSite("data/WebSiteKlein");
        DirectedGraph<String> webSiteGraph = buildGraphFromWebSite("data/WebSiteGross");
        System.out.println("Anzahl Seiten: \t" + webSiteGraph.getNumberOfVertexes());
        System.out.println("Anzahl Links: \t" + webSiteGraph.getNumberOfEdges());
        //System.out.println(webSiteGraph);

        // Starke Zusammenhangskomponenten berechnen und ausgeben
        StrongComponents<String> sc = new StrongComponents<>(webSiteGraph);
        System.out.println(sc.numberOfComp());
        //System.out.println(sc);

        // Page Rank ermitteln und Top-100 ausgeben
        pageRank(webSiteGraph);
    }

    /**
     * Liest aus dem Verzeichnis dirName alle Web-Seiten und
     * baut aus den Links einen gerichteten Graphen.
     *
     * @param dirName Name eines Verzeichnis
     * @return gerichteter Graph mit Namen der Web-Seiten als Knoten und Links als gerichtete Kanten.
     */
    private static DirectedGraph buildGraphFromWebSite(String dirName) throws IOException {
        File webSite = new File(dirName);
        DirectedGraph<String> webSiteGraph = new AdjacencyListDirectedGraph();

        for (File f : webSite.listFiles()) {
            String from = f.getName();
            LineNumberReader in = new LineNumberReader(new FileReader(f));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("href")) {
                    String[] s_arr = line.split("\"");
                    String to = s_arr[1];
                    webSiteGraph.addEdge(from, to);
                }
            }
        }
        return webSiteGraph;
    }

    /**
     * pageRank ermittelt Gewichte (Ranks) von Web-Seiten
     * aufgrund ihrer Link-Struktur und gibt sie aus.
     *
     * @param g gerichteter Graph mit Web-Seiten als Knoten und Links als Kanten.
     */
    private static <V> void pageRank(DirectedGraph<V> g) {
        int nI = 10;
        double alpha = 0.5;

        Map<V, Double> rankTable = new TreeMap<>();

        for (int i = 0; i < nI; i++) {
            for (V w: g.getVertexSet()) {
                if (!rankTable.containsKey(w)) {
                    rankTable.put(w, 1.0);
                } else {
                    double sum = 0;

                    for (V v: g.getPredecessorVertexSet(w)) {
                        sum += rankTable.getOrDefault(v, 1.0) / g.getSuccessorVertexSet(v).size();
                    }

                    rankTable.put(w, (1 - alpha) + alpha * sum);
                }
            }
        }

        System.out.println("Unsortiert:");

        for (Map.Entry<V, Double> entry : rankTable.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        List<Map.Entry<V, Double>> entries = new ArrayList<>(rankTable.entrySet());

        entries.sort(Map.Entry.comparingByValue());

        System.out.println("\n\nSortiert:");

        for (Map.Entry<V, Double> entry : entries) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println("\n\nTop-Seite:");

        System.out.println(entries.getLast().getKey() + ": " + entries.getLast().getValue());
    }
}
