/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

/**
 * Lorry platform class
 * @author Niels
 */
public class LorryPlatform extends Platform
{
    private Shipment m_lorryShipment = null;
    
    /**
     * Constructor
     * @param id id of platform
     */
    public LorryPlatform(int id){
        super(id);
    }
    
    /**
     * Sets a shipment to the lorry platform
     * @param shipment shipment
     * @throws Exception when already occupied
     */
    public void setShipment(Shipment shipment) throws Exception{
        if(hasShipment())
            throw new Exception("Shipment occupied");
        m_lorryShipment = shipment;
    }
    
    /**
     * Unsets a shipment
     */
    public void unsetShipment(){
        m_lorryShipment = null;
    }
    
    /**
     * Checks if platform has a shipment
     * @return true when has shipment, otherwise false
     */
    public boolean hasShipment(){
        return m_lorryShipment != null;
    }
    
    /**
     * Gets the shipment of the lorry
     * @return shipment
     */
    public Shipment getShipment(){
        return m_lorryShipment;
    }
}