/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Simulation.Behaviour;
import Simulation.Debug;
import Simulation.Main;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sietse
 */
public class GUI extends Behaviour{
    
    public static final float DEFAULT_TEXT_SIZE = 80.0f;
    
    // Singleton
    private static GUI m_instance;
    public static GUI instnace() {
        return m_instance;
    }
    
    //private static List<GuiItem> m_items = new ArrayList<GuiItem>();
    //private static long m_idCounter = 0;
    
    // Items
    private Picture m_logo;
    private BitmapText m_worldInfo;
    private BitmapText m_containerInfo; // print con
    // print container details
    // 
    
    public static Node root() {
        return Main.guiRoot();
    }
    public static Vector2f screenSize() {
        return new Vector2f(screenWidth(), screenHeight());
    }
    public static float screenHeight() {
        return Main.settings().getHeight();
    }
    public static float screenWidth() {
        return Main.settings().getWidth();
    }
    
    @Override
    public void awake() {
        m_instance = this;
    }
    @Override
    public void start() {
        
    }
    @Override
    public void rawUpdate() {
        /*
        for(GuiItem i : m_items) {
            //Debug.log(i.layer() + "");
            i._baseUpdate();
        }
        * */
        
        
        
    }
    
    
    
    
    /*
    public static long register(GuiItem item) {
        if (!m_items.contains(item)) {
            boolean _isAdded = false;
            for (int i = 0; i < m_items.size(); ++i) {
                if (m_items.get(i).layer() > item.layer()) {
                    _isAdded = true;
                    m_items.add(i, item);
                    break;
                }
            }
            if (!_isAdded) {
                m_items.add(item);
            }
        } else {
            return -1l;
        }
        return m_idCounter++;
    }
    public static boolean unregister(GuiItem item) {
        return m_items.remove(item);
    }
    * */
}
