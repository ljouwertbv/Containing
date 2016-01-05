package nhl.containing.simulator.game;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.SimulationItemType;
import nhl.containing.simulator.framework.Transform;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.world.WorldCreator;

/**
 * TODO: create()
 * @author sietse
 */
public class ParkingSpot extends Transform {
    
    private AGV m_agv;      // Container carrier
    private int m_arrivalPathID;
    private int m_departPathID;
    
     /**
     * Constructor
     * @param parent Platform
     * @param offset Offset to Platform
     * @param id id of the parent of this parkingspot
     */
    public ParkingSpot(Transform parent,Vector3f offset,int id,SimulationItemType type, int arrival, int depart){
        super(parent);
        m_agv = null;
        create();
        this.localPosition(offset);
        this.register(id,type,arrival,depart);
        m_arrivalPathID = arrival;
        m_departPathID = depart;
    }
    
    /**
     * Constructor
     * @param parent Platform
     * @param offset Offset to Platform
     */
    public ParkingSpot(Transform parent, Vector3f offset, SimulationItemType type, int arrival, int depart) {
        super(parent);
        m_agv = null;
        create();
        this.localPosition(offset);
        this.register(type,arrival,depart);
        m_arrivalPathID = arrival;
        m_departPathID = depart;
    }
    
    public int arrivalID() {
        return m_arrivalPathID;
    }
    public int departID() {
        return m_departPathID;
    }
    public void arrivalID(int i) {
        m_arrivalPathID = i;
    }
    public void departID(int i) {
        m_departPathID = i;
    }
    
    
    /**
     * Get AGV
     * @return 
     */
    public AGV agv() {
        return m_agv;
    }
    /**
     * Set AGV
     * @param _agv 
     */
    public void agv(AGV _agv) {
        this.m_agv = _agv;
    }
    /**
     * Visualize the parkingspot
     */
    private void create() {
        // TODO: Maybe shadow caster or decal
        WorldCreator.createBox(this, Utilities.one(), ColorRGBA.Black);
    }
}
