/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

/**
 * AGV Class
 * @author Niels
 */
public class AGV
{
    private final int m_id;
    private ShippingContainer m_container = null;
    private boolean m_isWaiting = true;
    private Node m_node = null;
    
    /**
     * Constructor
     * @param id id of the AGV
     */
    public AGV(int id){
        m_id = id;
    }
    
    /**
     * sets the current node
     * @param node node
     */
    public void setNode(Node node){
        m_node = node;
    }
    
    /**
     * Gets the current node
     * @return 
     */
    public Node getNode(){
        return m_node;
    }
    
    /**
     * Sets a container on an AGV
     * @param container container
     * @throws Exception when there is already a container
     */
    public void setContainer(ShippingContainer container) throws Exception{
        if(hasContainer())
            throw new Exception("Has already a container");
        m_container = container;
    }
    
    /**
     * Unsets a container
     */
    public void unsetContainer(){
        m_container = null;
    }
    
    /**
     * Gets the container
     * @return container when there is a container, otherwise null
     */
    public ShippingContainer getContainer(){
        return m_container;
    }
    
    /**
     * Checks if AGV has a container
     * @return true when there is a container, otherwise false
     */
    public boolean hasContainer(){
        return m_container != null;
    }
    
    /**
     * Sets the AGV to busy
     * @throws Exception when already moving
     */
    public void setBusy() throws Exception{
        if(isBusy())
            throw new Exception("AGV is already busy");
        m_isWaiting = false;
    }
    
    /**
     * Stops an AGV
     */
    public void stop(){
        m_isWaiting = true;
    }
    
    /**
     * Checks if AGV is moving
     * @return true when moving, otherwise false
     */
    public boolean isBusy(){
        return !m_isWaiting;
    }
    
    /**
     * Returns the ID of the AGV
     * @return ID
     */
    public int getID(){
        return m_id;
    }
}