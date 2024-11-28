// O. Bittel;
// 22.02.2017

package directedGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Klasse für Bestimmung aller strengen Komponenten.
 * Kosaraju-Sharir Algorithmus.
 * @author Oliver Bittel
 * @since 22.02.2017
 * @param <V> Knotentyp.
 */
public class StrongComponents<V> {
	private final Map<Integer,Set<V>> comp = new TreeMap<>();
	
	// Anzahl der Komponenten:
	private int numberOfComp = 0;
	
	/**
	 * Ermittelt alle strengen Komponenten mit
	 * dem Kosaraju-Sharir Algorithmus.
	 * @param g gerichteter Graph.
	 */
	public StrongComponents(DirectedGraph<V> g) {
		List<V> invPostOrder = new ArrayList<>();

		List<V> postOrder = new DepthFirstOrder<>(g).postOrder();

		for (int i = postOrder.size() - 1; i >= 0; i--) {
			invPostOrder.add(postOrder.get(i));
		}

		DirectedGraph<V> invG = g.invert();

		Set<V> visited = new HashSet<>();

		int i = 0;

		for (V v : invPostOrder) {
			if (!visited.contains(v)) {
				visitDF(i, invG, v, visited);

				numberOfComp++;

				++i;
			}
		}
	}

	private void visitDF(int component, DirectedGraph<V> g, V v, Set<V> visited) {
		visited.add(v);

		for (V w : g.getSuccessorVertexSet(v)) {
			if (!visited.contains(w)) {
				visitDF(component, g, w, visited);
			}
		}

		if (!comp.containsKey(component)) {
			comp.put(component, new TreeSet<>());
		}

		comp.get(component).add(v);
	}
	
	/**
	 * 
	 * @return Anzahl der strengen Komponeneten.
	 */
	public int numberOfComp() {
		return numberOfComp;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < comp.size(); ++i) {
			sb.append("Component ").append(i).append(": ");

			for (V v : comp.get(i)) {
				sb.append(v).append(", ");
			}

			sb.append("\n");
		}

		return sb.toString();
	}
	
		
	public static void main(String[] args) {
		DirectedGraph<Integer> g = new AdjacencyListDirectedGraph<>();
		g.addEdge(1,2);
		g.addEdge(1,3);
		g.addEdge(2,1);
		g.addEdge(2,3);
		g.addEdge(3,1);
		
		g.addEdge(1,4);
		g.addEdge(5,4);
		
		g.addEdge(5,7);
		g.addEdge(6,5);
		g.addEdge(7,6);
		
		g.addEdge(7,8);
		g.addEdge(8,2);
		
		StrongComponents<Integer> sc = new StrongComponents<>(g);
		
		System.out.println(sc.numberOfComp());  // 4
		
		System.out.println(sc);
			// Component 0: 5, 6, 7, 
        	// Component 1: 8, 
            // Component 2: 1, 2, 3, 
            // Component 3: 4, 
	}
}
