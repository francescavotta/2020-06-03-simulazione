package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	Graph <Player, DefaultWeightedEdge> grafo;
	Map <Integer, Player> idMap;
	Map <Integer, Player> giocatori;
	PremierLeagueDAO dao;
	Player migliore;
	List<Player> soluzioneMigliore;
	double gradoMax;
	
	public String creaGrafo(double soglia){
		dao = new PremierLeagueDAO();
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		giocatori = new HashMap<>();
		idMap = new HashMap<>();
		
		for(Player pp: dao.listAllPlayers()) {
			giocatori.put(pp.getPlayerID(), pp);
		}
		List<Player> vertici = dao.getVertici(soglia, giocatori);
		
		for(Player p: vertici) {
			idMap.put(p.getPlayerID(), p);
		}
		
		Graphs.addAllVertices(grafo, vertici);
		
		//aggiungo gli archi
		for(Adiacenza a : dao.getAdiacenze(idMap)) {
			if(grafo.containsEdge(a.getP1(), a.getP2()) || grafo.containsEdge(a.getP2(), a.getP1())) {
				
			}else {
				if(a.getPeso() < 0) {
					Graphs.addEdge(grafo, a.getP2(), a.getP1(), Math.abs(a.getPeso()));
				}else {
					Graphs.addEdge(grafo, a.getP1(), a.getP2(), a.getPeso());
				}
			}
		}
		
		
		return String.format("Il grafo è stato creato con %d vertici e %d archi", grafo.vertexSet().size(), grafo.edgeSet().size());
	}
	
	public Player topPlayer() {
		migliore = null;
		double numAvversari = 0;
		
		for(Player p: grafo.vertexSet()) {
			double num = grafo.outDegreeOf(p);
			
			
			if(num > numAvversari) {
				migliore = p;
				numAvversari = num;
			}
		}
		return migliore;
	}
	
	public List<PlayerBattuto> battuti(){
		List<PlayerBattuto> result = new ArrayList<>();
		for(DefaultWeightedEdge e: grafo.outgoingEdgesOf(migliore)) {
			result.add(new PlayerBattuto(grafo.getEdgeTarget(e), grafo.getEdgeWeight(e)));
		}
		Collections.sort(result);
		return result;
	}

	public List<Player> squadraDeiSogni(int numGiocatori){
		soluzioneMigliore = new ArrayList<>();
		gradoMax = 0;
		List<Player> parziale = new ArrayList<>();
		for(Player p: grafo.vertexSet()) {
			parziale.add(p);
			ricorsione(numGiocatori, parziale);
		}
		
		return soluzioneMigliore;
	}
	
	private void ricorsione(int numGiocatori, List<Player> parziale) {
		//casi terminali
		if(parziale.size() == numGiocatori) {
			if(calcolaGrado(parziale) > gradoMax ) {
				gradoMax = calcolaGrado(parziale);
				this.soluzioneMigliore = new ArrayList<>(parziale);
			}
			return;
		}

		//ricorsione vera e propria
		Player p = parziale.get(parziale.size()-1);
		for(DefaultWeightedEdge e: grafo.incomingEdgesOf(p)) {
			Player pp = Graphs.getOppositeVertex(grafo, e, p);
			if(!parziale.contains(pp)) {
				parziale.add(pp);
				ricorsione(numGiocatori, parziale);
				parziale.remove(parziale.size()-1);
			}
		}
	}

	
	private double calcolaGrado(List<Player> parziale) {
		double somma = 0;
		
		for(Player p: parziale) {
			for(DefaultWeightedEdge e: grafo.outgoingEdgesOf(p)) {
				somma = somma + grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e: grafo.incomingEdgesOf(p)) {
				somma = somma - grafo.getEdgeWeight(e);
			}
		}
		return somma;
	}

	public List<Player> getSoluzioneMigliore() {
		return soluzioneMigliore;
	}

	public double getGradoMax() {
		return gradoMax;
	}
	
	
}
