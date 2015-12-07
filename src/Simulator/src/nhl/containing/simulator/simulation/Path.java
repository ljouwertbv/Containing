/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;

import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public class Path {
    
    // Main
    private Vector3f[] m_nodes = new Vector3f[0];       // Path node positions
    private Vector3f m_previousPosition = Vector3f.ZERO;// Previous position, used for switching between nodes
    private int m_targetNode = -1;                      // Target node
    
    // Settings
    private boolean m_manual = false;                   // Manual update
    private boolean m_useTimeInsteadOfSpeed = false;    // true -> Use time based | false -> Use speed based
    
    // Behaviours
    private float m_speed = 1.0f;                       // Speed
    private float m_waitTime = 0.0f;                    // Wait time at node
    private LoopMode m_loopMode = LoopMode.Loop;        // Loop mode
    private EaseType m_easeType = EaseType.Linear;      // Ease type (interpolation type)
    private Callback m_callback = null;                 // Callback at node
    
    // Other
    private float m_timer = 0.0f;                       // Move timer
    private boolean m_goBack = false;                   // Go inverse direction
    
    
    /**
     * Use a null for default value
     * @param currentPosition
     * @param startNode
     * @param manual
     * @param useSpeed
     * @param speed
     * @param waitTime
     * @param loopMode
     * @param easeType
     * @param callback
     * @param nodes 
     */
    public Path(Vector3f currentPosition, Integer startNode, boolean manual, boolean useSpeed, float speed, Float waitTime, LoopMode loopMode, EaseType easeType, Callback callback, Vector3f... nodes) {
        init(currentPosition, startNode, manual, useSpeed, speed, waitTime, loopMode, easeType, callback, nodes);
    }
    
    /**
     * Constructor extention to reduce code
     * @param currentPosition
     * @param startNode
     * @param manual
     * @param useSpeed
     * @param speed
     * @param waitTime
     * @param loopMode
     * @param easeType
     * @param callback
     * @param nodes 
     */
    private void init(Vector3f currentPosition, Integer startNode, boolean manual, boolean useSpeed, float speed, Float waitTime, LoopMode loopMode, EaseType easeType, Callback callback, Vector3f... nodes) {
        
        this.m_manual = manual;
        this.m_useTimeInsteadOfSpeed = !useSpeed;
        this.m_speed = speed;
        this.m_waitTime = waitTime == null ? 0.0f : waitTime;
        this.m_loopMode = loopMode == null ? LoopMode.PingPong : loopMode;
        this.m_easeType = easeType == null ? EaseType.Linear : easeType;
        this.m_callback = callback;
        
        setPathf(new Vector3f(currentPosition == null ? (nodes.length < 1 ? Vector3f.ZERO : nodes[0]) : currentPosition), nodes);
        this.m_targetNode = startNode == null ? 0 : startNode;
    }
    
    /**
     * Set path
     * @param nodes 
     */
    public void setPath(Vector3f... nodes) {
        setPathf(getPosition(), nodes);
    }
    /**
     * Set path raw
     * @param from
     * @param nodes 
     */
    public void setPathf(Vector3f from, Vector3f... nodes) {
        setPosition(from);
        
        // Reset
        this.m_timer = 0.0f;
        this.m_goBack = false;
        this.m_targetNode = 0;
        
        // Clone nodes
        Vector3f[] __nodes = new Vector3f[nodes.length];
        for(int i = 0; i < nodes.length; i++)
            __nodes[i] = new Vector3f(nodes[i]);
        
        // Set
        m_nodes = __nodes;
    }
    
    /**
     * Update this every frame
     */
    public void update() {
        if (m_timer < 1.0f) { // Stage 1: move
            m_timer += m_useTimeInsteadOfSpeed ? Time.deltaTime() / m_speed : Time.deltaTime() * Mathf.min(Utilities.NaNSafeFloat(m_speed / Utilities.distance(m_previousPosition, m_nodes[m_targetNode])), 1.0f);
            if (m_timer >= 1.0f && m_callback != null)
                m_callback.invoke();
        }
        else if (m_timer < 1.0f + m_waitTime) { // Stage 2: wait
            m_timer += Time.deltaTime();
        }
        else {
            if (!m_manual) { // Stage 3: to next
                next();
                m_timer -= (1.0f + m_waitTime);
            }
        }
    }
    /**
     * Set target to next point
     */
    public void next() {
        savePosition();
        
        if (m_manual)
            m_timer = 0.0f;
        
        if (m_nodes.length < 2)
            return;
        
        switch(m_loopMode) {
            case Loop: m_targetNode = (m_targetNode + 1) % m_nodes.length; break;
            case Once: if (m_targetNode < m_nodes.length - 1) m_targetNode++; break;
            case PingPong:
                if (m_goBack) {
                    if (--m_targetNode < 0) {
                        m_targetNode = 1;
                        m_goBack = false;
                    }
                }
                else if (++m_targetNode >= m_nodes.length) {
                    m_targetNode = m_nodes.length - 2;
                    m_goBack = true;
                }
                break;
        }
    }
    /**
     * Set target index
     * @param target 
     */
    public void setTarget(int target) {
        m_targetNode = target;
    }
    /**
     * safe position from interpolation
     */
    private void savePosition() {
        setPosition(getPosition());
    }
    /**
     * Get the current positio
     * @return 
     */
    public Vector3f getPosition() {
        return Interpolate.ease(m_easeType, m_previousPosition, m_nodes[m_targetNode], m_timer);
    }

    /**
     * Set position
     * @param position 
     */
    public void setPosition(Vector3f position) {
        m_previousPosition = position.clone();
    }
    /**
     * Set the callback at wait start
     * @param callback 
     */
    public void setCallback(Callback callback) {
        m_callback = callback;
    }
    /**
     * Set speed
     * @param speed 
     */
    public void setSpeed(float speed) {
        this.m_speed = speed;
    }
    /**
     * Is at first node
     * @return 
     */
    public boolean atFirst() {
        return atFirst(0.001f);
    }
    /**
     * Is at first node
     * @param range
     * @return 
     */
    public boolean atFirst(float range) {
        return (new Vector3f(m_nodes[0]).distanceSquared(getPosition()) < range * range);
    }
    /**
     * Is at last node
     * @return 
     */
    public boolean atLast() {
        return atLast(0.001f);
    }
    /**
     * Is at last node
     * @param range
     * @return 
     */
    public boolean atLast(float range) {
        return (new Vector3f(m_nodes[m_nodes.length - 1]).distanceSquared(getPosition()) < range * range);
    }
    /**
     * Get current target node
     * @return 
     */
    public int getTargetIndex() {
        return m_targetNode;
    }
    /**
     * Finished waitng
     * @return 
     */
    public boolean finishedWaiting() {
        return m_timer >= 1.0f + m_waitTime;
    }
}