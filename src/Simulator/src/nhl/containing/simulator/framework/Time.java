package nhl.containing.simulator.framework;

import nhl.containing.simulator.networking.SimulatorClient;
import nhl.containing.simulator.simulation.Main;

/**
 * Time class to record time, timescale, scaledtime, deltatime
 * 
 * @author sietse
 */
public final class Time {
    private static float m_timeScale = 1.0f;                    // Current time scale
    private static float m_fixedTimeScale = 0.02f;              // 50 fps (fps = 1.0 / m_fixedTimeScale)
    private static float m_deltaTime = 0.0f;                    // Time between the previous and the current frame
    private static float m_time = 0.0f;                         // Time in simulator
    
    private static final float MIN_FIXED_TIME_SCALE = 0.005f;   // Minimum fixed time scale
    private static final float TIME_SEND_INTERVAL = 10f;         // Time interfal when sending to controller
    private static float m_deltaSum = 0;                        // Send timer
    
    /**
     * Update time, only call this from main
     * @param deltaTime 
     */
    public static void _updateTime(float deltaTime) {
        
        // Add deltatime to total time
        float _addedTime = deltaTime * m_timeScale;
        m_deltaTime = deltaTime;
        m_time += _addedTime;
        
        if(SimulatorClient.controllerCom != null) {
            m_deltaSum += _addedTime;
            
            // Send
            if(m_deltaSum >= TIME_SEND_INTERVAL) {
                Main.instance().simClient().sendTimeUpdate();
                m_deltaSum = 0;
            } 
        }
        
    }
    
    /**
     * Sets the time from the controller
     * @param time 
     */
    public static void setTime(float time) {
        m_time = time;
    }
    
    /**
     * Set simulation timescale
     * @param t 
     */
    public static void setTimeScale(float t) {
        m_timeScale = Mathf.max(t, 0.0f);
        //System.out.println("New time scale: " + t);
    }
    /**
     * Set fixedtimescale
     * @param t 
     */
    public static void setFixedTimeScale(float t) {
        m_fixedTimeScale = Mathf.max(MIN_FIXED_TIME_SCALE, t);
    }
    
    /**
     * Get timescale
     * @return 
     */
    public static float timeScale() {
        return m_timeScale;
    }
    /***
     * Get fixedtmescale
     * @return 
     */
    public static float fixedTimeScale() {
        return m_fixedTimeScale;
    }
    
    /**
     * Get deltatime unscaled
     * @return 
     */
    public static float unscaledDeltaTime() {
        return m_deltaTime;
    }
    /**
     * Get deltatime scaled
     * @return 
     */
    public static float deltaTime() {
        return (m_deltaTime * m_timeScale);
    }
    
    /**
     * Get fixedDeltatime scaled
     * @return 
     */
    public static float fixedDeltaTime() {
        return (m_fixedTimeScale * m_timeScale);
    }
    
    /**
     * Get time in simulator
     * @return 
     */
    public static float time() {
        return m_time;
    }
}
