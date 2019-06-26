package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idAirportMap;
    private Graph<Airport, DefaultWeightedEdge> grafo;
    private List<Airport> vicini;
    private List<Airport> result;
	
	public Model() {
		
		this.dao = new ExtFlightDelaysDAO();
	}
	
	public void creaGrafo(int compagnie) {
		// TODO Auto-generated method stub
	
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.idAirportMap = new HashMap<Integer, Airport>();
		
		
		// carico i vertici 
		dao.loadVertex(grafo, idAirportMap, compagnie);
		//carico gli archi
		dao.loadEdges(grafo, idAirportMap, compagnie);
		
		System.out.println("Grafo creato : "+grafo.vertexSet().size() +" - "+grafo.edgeSet().size());
	}

	public List<Airport> getVertex() {
		if(grafo!=null) {
			
			List<Airport> ris = new LinkedList<Airport>(grafo.vertexSet());
			Collections.sort(ris);
			return ris;
		}
			
		return null;
	}

	public List<AirportPlane> cercaVicini(Airport value) {
		
		if(grafo!=null) {
			
			List<AirportPlane> ris = new LinkedList<AirportPlane>();
			
			for(Airport vicino : Graphs.neighborListOf(grafo, value)){
				
				ris.add(new AirportPlane(vicino, (int) grafo.getEdgeWeight(grafo.getEdge(value, vicino))));
				
			}
			
			Collections.sort(ris);
			return ris;
			
			
		}
		
		return new ArrayList<AirportPlane>();
	}

	public boolean isConnected(Airport partenza, Airport destinazione) {
		
		ConnectivityInspector<Airport, DefaultWeightedEdge> ci = new ConnectivityInspector<>(grafo);
		
		return ci.pathExists(partenza, destinazione);
	}
	
	public int minTratta(Airport partenza, Airport destinazione) {
		DijkstraShortestPath<Airport, DefaultWeightedEdge> dij = new DijkstraShortestPath<>(grafo);
		return (dij.getPath(partenza, destinazione).getVertexList().size()-1);
	}

	public List<Airport> getPath(Airport partenza, Airport destinazione, int t) {
	
		result = new ArrayList<Airport>();
		result.add(partenza);
		 //serve una lista perchè potrebbe visitare due volte lo stesso aereoporto 
		List<Airport> parziale = new ArrayList<Airport>();
		parziale.add(partenza);
		
		
		
		cerca(parziale, 0, t, destinazione);
		
		return result;
	}

	private void cerca(List<Airport> parziale, int l, int t, Airport destinazione) {
	
		//condizione di terminazione
		
         if(t==l || parziale.get(parziale.size()-1).equals(destinazione)) {
			if(parziale.get(parziale.size()-1).equals(destinazione) && peso(parziale)>peso(result)) 
				result = new ArrayList<Airport>(parziale);
			return;
		}
		
	    vicini = new ArrayList<Airport>(Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1)));
		//per ogni vicino 
		for(int i=0; i<vicini.size(); i++) {
			
			//aggiungiamo aereoporto e tratta
				
			parziale.add(vicini.get(i));
			
			cerca(parziale, l+1, t, destinazione);
			
			parziale.remove(parziale.size()-1);
			
		}
		
		
		
	}

	private int peso(List<Airport> parziale) {
		 if(parziale.size()<=1)
	     	return 0;
		 int peso = 0;
		 for(int i = 0; i<parziale.size()-1; i++)  {
			 
			 if(grafo.getEdge(parziale.get(i), parziale.get(i+1))!=null)
				 peso+= (int) grafo.getEdgeWeight(grafo.getEdge(parziale.get(i), parziale.get(i+1)));
			 else if(grafo.getEdge(parziale.get(i+1), parziale.get(i))!=null)
				 peso+= (int) grafo.getEdgeWeight(grafo.getEdge(parziale.get(i+1), parziale.get(i)));	
		 } 
		 return peso;	 
	}

	
	
}
