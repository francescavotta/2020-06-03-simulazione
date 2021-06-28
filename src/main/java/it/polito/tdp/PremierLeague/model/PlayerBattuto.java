package it.polito.tdp.PremierLeague.model;

public class PlayerBattuto implements Comparable<PlayerBattuto>{

	private Player p;
	private double diff;
	public Player getP() {
		return p;
	}
	public void setP(Player p) {
		this.p = p;
	}
	public double getDiff() {
		return diff;
	}
	public void setDiff(double diff) {
		this.diff = diff;
	}
	public PlayerBattuto(Player p, double diff) {
		super();
		this.p = p;
		this.diff = diff;
	}
	@Override
	public int compareTo(PlayerBattuto o) {
		
		return -(Double.compare(this.diff, o.diff));
	}
	@Override
	public String toString() {
		return " " + p.getPlayerID() + " - " + p.getName() + ", diff=" + diff + "\n";
	}
	
	
	
	
}
