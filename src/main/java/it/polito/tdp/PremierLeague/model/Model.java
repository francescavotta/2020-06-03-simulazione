package it.polito.tdp.PremierLeague.model;

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
	
	public String creaGrafo(double soglia){
		dao = new PremierLeagueDAO();
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		for(Player pp: dao.listAllPlayers()) {
			giocatori.put(pp.getPlayerID(), pp);
		}
		List<Player> vertici = dao.getVertici(soglia, giocatori);
		
		for(Player p: vertici) {
			idMap.put(p.getPlayerID(), p);
		}
		
		Graphs.addAllVertices(grafo, vertici);
		return null;
	}

}
