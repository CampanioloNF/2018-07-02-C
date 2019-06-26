package it.polito.tdp.extflightdelays.model;

public class AirportPlane implements Comparable<AirportPlane>{

	private Airport a1;
	private int voli;
	public AirportPlane(Airport a1, int voli) {
		super();
		this.a1 = a1;
		this.voli = voli;
	}
	public Airport getA1() {
		return a1;
	}
	public int getVoli() {
		return voli;
	}
	@Override
	public int compareTo(AirportPlane o) {
		// TODO Auto-generated method stub
		return o.voli-this.voli;
	}
	
	
}
