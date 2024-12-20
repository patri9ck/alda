// O. Bittel;
// 30.7.2024

package directedGraph;

import java.util.*;

/**
 * Klasse zur Ermittlung von gerichteten Zyklen.
 * @author Oliver Bittel
 * @since 30.7.2024
 * @param <V> Knotentyp.
 */
public class DirectedCycle<V> {
	// ...
	private final List<V> cycle = new LinkedList<>();	// ein Zyklus, falls vorhanden
	private final DirectedGraph<V> myGraph;

	/**
	 * Führt eine Tiefensuche für g durch und prüft dabei auf Zyklen.
	 * Falls ein Zyklus erkannt wird, wird die Suche abgebrochen.
	 * @param g gerichteter Graph.
	 */
	public DirectedCycle(DirectedGraph<V> g) {
		myGraph = g;

		Set<V> visited = new HashSet<>();
		Stack<V> path = new Stack<>();
		Set<V> nodeInPath = new HashSet<>();

		for (V v : myGraph.getVertexSet()) {
			if (!visited.contains(v)) {
				searchDirectedCycle(v, visited, path, nodeInPath);

				if (hasCycle()) {
					return;
				}
			}
		}
	}

	private void searchDirectedCycle(V v, Set<V> visited, Stack<V> path, Set<V> nodeInPath) {
		visited.add(v);
		path.push(v);
		nodeInPath.add(v);

		for (V w : myGraph.getSuccessorVertexSet(v)) {
			if (!visited.contains(w) ) {
				searchDirectedCycle(w, visited, path, nodeInPath);
			} else if (nodeInPath.contains(w)) {
				cycle.addAll(nodeInPath);
			}
		}

		path.pop();
		nodeInPath.remove(v);
	}

	
	/**
	 * Liefert einen Zyklus zurück, falls ein Zyklus vorhanden ist.
	 * @return Zyklus falls vorhanden, sonst null.
	 */
	public List<V> getCycle(){
		return cycle.isEmpty()? null : Collections.unmodifiableList(cycle);
	}
	
	/**
	 * Prüft ob Zyklus vorhanden ist.
	 * @return true, falls Zyklus vorhanden ist, sonst falls.
	 */
	public boolean hasCycle(){
		return !cycle.isEmpty();
	}

	
	public static void main(String[] args) {
		DirectedGraph<Integer> g = new AdjacencyListDirectedGraph<>();
		g.addEdge(1,2);
		g.addEdge(2,5);
		g.addEdge(5,1);
		g.addEdge(2,6);
		g.addEdge(3,7);
		g.addEdge(4,3);
		g.addEdge(4,6);
		g.addEdge(7,4);
		
		DirectedCycle<Integer> dc = new DirectedCycle<>(g);
		System.out.println(dc.hasCycle());
		System.out.println(dc.getCycle());
	}
}
