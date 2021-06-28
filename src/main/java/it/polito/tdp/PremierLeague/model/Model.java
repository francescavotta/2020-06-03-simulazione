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
	int bestDegree;
	List<Player> dreamTeam;
	
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
		List<Player> giocatoriRimasti = new ArrayList<>(grafo.vertexSet());
		ricorsione(numGiocatori, parziale, giocatoriRimasti);
		
		return soluzioneMigliore;
	}
	
	private void ricorsione(int numGiocatori, List<Player> parziale, List<Player> giocatoriRimasti) {
		//casi terminali
		if(parziale.size() == numGiocatori) {
			if(calcolaGrado(parziale) > gradoMax ) {
				gradoMax = calcolaGrado(parziale);
				this.soluzioneMigliore = new ArrayList<>(parziale);
			}
			return;
		}

		//ricorsione vera e propria
		for(Player p: giocatoriRimasti) {
			if(!parziale.contains(p)) {
			parziale.add(p);
			List<Player> gioc = new ArrayList<>(giocatoriRimasti);
			gioc.removeAll(Graphs.successorListOf(grafo, p));
			ricorsione(numGiocatori, parziale, gioc);
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
	
	public List<Player> getDreamTeam(int k){
		this.bestDegree = 0;
		this.dreamTeam = new ArrayList<Player>();
		List<Player> partial = new ArrayList<Player>();
		
		this.recursive(partial, new ArrayList<Player>(this.grafo.vertexSet()), k);

		return dreamTeam;
	}
	
	public void recursive(List<Player> partial, List<Player> players, int k) {
		if(partial.size() == k) {
			int degree = this.getDegree(partial);
			if(degree > this.bestDegree) {
				dreamTeam = new ArrayList<>(partial);
				bestDegree = degree;
			}
			return;
		}
		
		for(Player p : players) {
			if(!partial.contains(p)) {
				partial.add(p);
				//i "battuti" di p non possono più essere considerati
				List<Player> remainingPlayers = new ArrayList<>(players);
				remainingPlayers.removeAll(Graphs.successorListOf(grafo, p));
				recursive(partial, remainingPlayers, k);
				partial.remove(p);
				
			}
		}
	}
	
	private int getDegree(List<Player> team) {
		int degree = 0;
		int in;
		int out;

		for(Player p : team) {
			in = 0;
			out = 0;
			for(DefaultWeightedEdge edge : this.grafo.incomingEdgesOf(p))
				in += (int) this.grafo.getEdgeWeight(edge);
			
			for(DefaultWeightedEdge edge : grafo.outgoingEdgesOf(p))
				out += (int) grafo.getEdgeWeight(edge);
		
			degree += (out-in);
		}
		return degree;
	}
	
	public Integer getBestDegree() {
		return bestDegree;
	}
	
	
}
