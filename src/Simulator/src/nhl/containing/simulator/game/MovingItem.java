package nhl.containing.simulator.game;

import nhl.containing.simulator.framework.Path;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.framework.Transform;

/**
 * Used for everything that moves
 * @author sietse
 */
public class MovingItem extends ContainerCarrier {
    protected float m_empySpeed;        // The speed when it doesn't carrie anything
    protected float m_loadedSpeed;      // The speed when it is carring a container
    
    private Path m_path;                // Path
    
    /**
     * Constructor
     */
    public MovingItem() {
        super();
    }
    /**
     * Constructor
     * @param parent 
     */
    public MovingItem(Transform parent) {
        super(parent, Point3.one());
    }
    /**
     * Constructor
     * @param parent
     * @param loadedSpeed
     * @param emptySpeed 
     */
    public MovingItem(Transform parent, float loadedSpeed, float emptySpeed) {
        super(parent, Point3.one());
        this.m_empySpeed = emptySpeed;
        this.m_loadedSpeed = loadedSpeed;
    }
    /**
     * Constructor
     * @param parent
     * @param loadedSpeed
     * @param emptySpeed
     * @param path 
     */
    public MovingItem(Transform parent, float loadedSpeed, float emptySpeed, Path path) {
        super(parent, Point3.one());
        this.m_empySpeed = emptySpeed;
        this.m_loadedSpeed = loadedSpeed;
    }
    /**
     * Constructor
     * @param size
     * @param speed 
     */
    public MovingItem(Point3 size, float speed) {
        super(null, size);
        this.m_empySpeed = this.m_loadedSpeed = speed;
    }
    
    /**
     * Set path
     * @param path 
     */
    protected void path(Path path) {
        m_path = path;
    }
    /**
     * Get path
     * @return 
     */
    public Path path() {
        return m_path;
    }
}
