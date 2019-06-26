package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Flight;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				result.add(airport);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public void loadVertex(Graph<Airport, DefaultWeightedEdge> grafo, Map<Integer, Airport> idAirportMap,
			int compagnie) {
		
		String sql = "SELECT a1.ID, a1.IATA_CODE, a1.AIRPORT, a1.CITY, a1.STATE, a1.COUNTRY, a1.LATITUDE, a1.LONGITUDE, a1.TIMEZONE_OFFSET " + 
				"FROM airports a1, flights f " + 
				"WHERE a1.ID = f.ORIGIN_AIRPORT_ID OR a1.ID = f.DESTINATION_AIRPORT_ID " + 
				"GROUP BY a1.ID " + 
				"HAVING COUNT(DISTINCT(f.AIRLINE_ID)) >= ? ";
		
		try {
			
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, compagnie);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				Airport air = new Airport(rs.getInt("a1.ID"), rs.getString("a1.IATA_CODE"), rs.getString("a1.AIRPORT"),
						rs.getString("a1.CITY"), rs.getString("a1.STATE"), rs.getString("a1.COUNTRY"), rs.getDouble("a1.LATITUDE"),
						rs.getDouble("a1.LONGITUDE"), rs.getDouble("a1.TIMEZONE_OFFSET"));
				
				if(!idAirportMap.containsKey(air.getId())) {
					
					idAirportMap.put(air.getId(), air);
					grafo.addVertex(air);
					
				}
				
			}

			conn.close();
			

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			
		}
		
	}

	public void loadEdges(Graph<Airport, DefaultWeightedEdge> grafo, Map<Integer, Airport> idAirportMap,
			int compagnie) {
		
		String sql = "SELECT a1.ID, a2.ID, COUNT(*) AS voli " + 
				"FROM flights f, airports a1, airports a2 " + 
				"WHERE f.ORIGIN_AIRPORT_ID = a1.ID AND f.DESTINATION_AIRPORT_ID = a2.ID " + 
				"GROUP BY a1.ID, a2.ID";
		
		try {
			
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				
				// se gli aeroporti sono tra i vertici
				
					
					Airport a1 = idAirportMap.get(rs.getInt("a1.ID"));
					Airport a2 = idAirportMap.get(rs.getInt("a2.ID"));
				    int voli = rs.getInt("voli");
					if(a1!=null  && a2!=null) {
					 Graphs.addEdge(grafo, a1, a2, voli);
					  
					
					   
					}
				
			}

			conn.close();
			

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			
		}
		
	}
}
