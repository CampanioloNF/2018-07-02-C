/**
 * Sample Skeleton for 'ExtFlightDelays.fxml' Controller Class
 */

package it.polito.tdp.extflightdelays;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.AirportPlane;
import it.polito.tdp.extflightdelays.model.Model;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ExtFlightDelaysController {

	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML // fx:id="compagnieMinimo"
    private TextField compagnieMinimo; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalizza"
    private Button btnAnalizza; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBoxAeroportoPartenza"
    private ComboBox<Airport> cmbBoxAeroportoPartenza; // Value injected by FXMLLoader

    @FXML // fx:id="btnAeroportiConnessi"
    private Button btnAeroportiConnessi; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBoxAeroportoDestinazione"
    private ComboBox<Airport> cmbBoxAeroportoDestinazione; // Value injected by FXMLLoader

    @FXML // fx:id="numeroTratteTxtInput"
    private TextField numeroTratteTxtInput; // Value injected by FXMLLoader

    @FXML // fx:id="btnCercaItinerario"
    private Button btnCercaItinerario; // Value injected by FXMLLoader

    private int compagnie = 0;
    
    @FXML
    void doAnalizzaAeroporti(ActionEvent event) {

    	txtResult.clear();
    	try {
    		
            compagnie = Integer.parseInt(this.compagnieMinimo.getText());
            if(compagnie<=0)
            	throw new NumberFormatException();
            model.creaGrafo(compagnie);
            
            cmbBoxAeroportoPartenza.setItems(FXCollections.observableList(model.getVertex()));
            cmbBoxAeroportoDestinazione.setItems(FXCollections.observableList(model.getVertex()));
            
    		
    	}catch(NumberFormatException nfe) {
    		txtResult.appendText("Errore. Inserire un numero intero positivo nel campo 'Compagnie minime'. Grazie.");
    	}
    	
    }

    @FXML
    void doCalcolaAeroportiConnessi(ActionEvent event) {

    	txtResult.clear();
    	
        try {
        	
        	txtResult.appendText("Aereoporti vicini:\n");
        	
        	for(AirportPlane vicino : model.cercaVicini(cmbBoxAeroportoPartenza.getValue())) {
        		
        		txtResult.appendText(vicino.getA1()+"  voli("+vicino.getVoli()+")\n");
        		
        	}
        	
        }catch(NullPointerException npe) {
          txtResult.appendText("Selezionare un aereoporto, grazie.");
        }
    	
    }

    @FXML
    void doCercaItinerario(ActionEvent event) {

    	txtResult.clear();  
    	
    	try {
    		
    		Airport partenza = cmbBoxAeroportoPartenza.getValue();
    		Airport destinazione = cmbBoxAeroportoDestinazione.getValue();
          	
    		int t = Integer.parseInt(this.numeroTratteTxtInput.getText());
    		  if(t<=0)
    			  throw new NumberFormatException();
          	if(t<model.minTratta(partenza, destinazione)) {
          		txtResult.appendText("per raggiungere tale destinazione sono necessarie alemeno "+model.minTratta(partenza, destinazione)+" tratte");
          	    return;
          	}
    		  
          	if(model.isConnected(partenza, destinazione)) {
          		txtResult.appendText("Percorso ottimo:\n");
              	for(Airport air : model.getPath(partenza, destinazione, t))
              		txtResult.appendText(air+"\n");
          	}else
          		txtResult.appendText("I due aereoporti non sono connessi.");
          	
          	
          	          	
          }catch(NullPointerException npe) {
           txtResult.appendText("Selezionare un aereoporto di partenza ed uno di destinazione, grazie.");
          }
    	  catch(NumberFormatException nfe) {
            txtResult.appendText("Selezionare un numero di tratte (intero positivo) , grazie.");
          }
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert compagnieMinimo != null : "fx:id=\"compagnieMinimo\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert cmbBoxAeroportoPartenza != null : "fx:id=\"cmbBoxAeroportoPartenza\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnAeroportiConnessi != null : "fx:id=\"btnAeroportiConnessi\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert cmbBoxAeroportoDestinazione != null : "fx:id=\"cmbBoxAeroportoDestinazione\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert numeroTratteTxtInput != null : "fx:id=\"numeroTratteTxtInput\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";
        assert btnCercaItinerario != null : "fx:id=\"btnCercaItinerario\" was not injected: check your FXML file 'ExtFlightDelays.fxml'.";

    }
    
    
    public void setModel(Model model) {
  		this.model = model;
  		
  	}
}
