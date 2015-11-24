package nhl.containing.simulator.simulation;

import nhl.containing.simulator.world.World;
import nhl.containing.simulator.world.TestBehaviour;
import nhl.containing.simulator.utils.Line3DNode;
import nhl.containing.simulator.utils.MaterialCreator;
import nhl.containing.simulator.utils.Line3D;
import nhl.containing.simulator.networking.InstructionDispatcherSimulator;
import nhl.containing.simulator.networking.SimulatorClient;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;

/**
 * 
 * @author sietse
 */
public class Main extends SimpleApplication {

    // Singleton
    private static Main m_instance;
    public static Main instance() {
        return m_instance;
    }

    // 
    private float m_previousTimeScale = 1.0f;
    
    // Behaviours
    private static List<Behaviour> m_behaviours;
    private float m_fixedUpdateTimer = 0.0f;
    
    // Lines
    private static List<Line3D> m_lines;
    
    private Input m_input;
    
    // Camera
    private nhl.containing.simulator.game.Camera m_camera;
    public com.jme3.renderer.Camera cam() {
        return cam;
    }
    public nhl.containing.simulator.game.Camera camera() {
        return m_camera;
    }
    public FlyByCamera flyCamera() {
        return flyCam;
    }
    public void showCursor(boolean show) {
        inputManager.setCursorVisible(show);
    }
    public Vector2f cursorPosition() {
        return inputManager.getCursorPosition();
    }
    
    //Networking
    SimulatorClient _simClient;
    InstructionDispatcherSimulator _dispatcher;
    
    public SimulatorClient simClient()
    {
        return _simClient;
    }
    
    // Input
    public static InputManager inputManager() {
        return instance().inputManager;
    }
    public static AssetManager assets() {
        return instance().assetManager;
    }
    public static Node root() {
        return instance().rootNode;
    }
    public static ViewPort view() {
        return instance().viewPort;
    }
    public static Input input() {
        return instance().m_input;
    }
    
    /** HERE COME ALL BEHAVIOURS
     * 
     * 
     * 
     * 
     */
    private void initBehaviours() {
        // Init main behaviours
        m_camera = new nhl.containing.simulator.game.Camera();
        m_input = new Input();
        
        // Init all behaviours
        Behaviour[] behaviours = new Behaviour[] {
            m_input,
            m_camera,
            
            // Non-Main
            new World(),
            new TestBehaviour()
        };
        // Init all behaviours
        for (Behaviour behaviour : behaviours) {
            behaviour._baseInit();
        }
    }
    /**
     * Create world here
     */
    private void initWorld() {
        m_lines = new ArrayList<Line3D>();
        Line3D[] __t = new Line3D[] {
            new Line3D(
                MaterialCreator.diffuse(new ColorRGBA(0.4f, 0.6f, 0.8f, 1.0f)),
                new Line3DNode(new Vector3f(0.0f, 00.0f, 00.0f), 0.1f, ColorRGBA.Blue), 
                new Line3DNode(new Vector3f(0.0f, 10.0f, 00.0f), 1.0f, ColorRGBA.Blue),
                new Line3DNode(new Vector3f(0.0f, 10.0f, 10.0f), 1.0f, ColorRGBA.Blue))
        };
        m_lines.addAll(Arrays.asList(__t));
    }
    private void updateWorld() {
        for (Line3D l : m_lines) {
            l.UpdateMesh();
        }
    }
    
    /**
     * Called at init
     */
    @Override
    public void simpleInitApp() {
        m_instance = this;
        m_behaviours = new ArrayList<Behaviour>();
        initBehaviours();
        initWorld();
    }
    /**
     * Called every frame
     * @param tpf deltaTime
     */
    @Override
    public void simpleUpdate(float tpf) {
        
        Time._updateTime(tpf);
        updateBehaviours();
        updateWorld();
        updateTimescale();
    }
    /**
     * Called on render
     * @param rm Renderer
     */
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    /**
     * Register behaviour
     * @param behaviour Behaviour to register
     * @return Register success
     */
    public static boolean Register(Behaviour behaviour) {
        if (!m_behaviours.contains(behaviour)) {
            m_behaviours.add(behaviour);
            behaviour.awake();
            return true;
        }
        return false;
    }
    /**
     * Unregister behaviour
     * @param behaviour behaviour to unregister
     * @return unregister success
     */
    public static boolean Unregister(Behaviour behaviour) {
        return m_behaviours.remove(behaviour);
    }
    /**
     * Updates all behaviours
     */
    private void updateBehaviours() {
        m_fixedUpdateTimer += Time.deltaTime();
        
        // Update
        for (Behaviour behaviour : m_behaviours) {
            behaviour._baseUpdate();
        }
        
        // FixedUpdate
        while(m_fixedUpdateTimer >= Time.fixedTimeScale()) {
            m_fixedUpdateTimer -= Time.fixedTimeScale();
            for (Behaviour behaviour : m_behaviours) {
                behaviour._baseFixedUpdate();
            }
        }
        
        // LateUpdate
        for (Behaviour behaviour : m_behaviours) {
            behaviour._baseLateUpdate();
        }
    }
    
    public static void main(String[] args) {     
        Logger.getLogger("").setLevel(Level.SEVERE);
        Main app = new Main();
        app.showSettings = false;
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 600);
        settings.setBitsPerPixel(32);
        app.setSettings(settings);
        app.start();
        
        //Init networking
        app._simClient = new SimulatorClient();
        app._dispatcher = new InstructionDispatcherSimulator(app);
        app._simClient.getComProtocol().setDispatcher(app._dispatcher);
        new Thread(app._simClient).start();
    }
    
    public void togglePause() {
        if (Time.timeScale() < 0.001f) {
            // unpause
            Time.setTimeScale(m_previousTimeScale);
        } else {
            m_previousTimeScale = Time.timeScale();
            Time.setTimeScale(0.0f);
        }
    }
    public void updateTimescale() {
        float __temp = 10.0f * Time.deltaTime();
        
        if (m_input.getButton("R").isDown())
            __temp = -__temp;
        else if (!m_input.getButton("T").isDown())
            return;
        
        __temp += Time.timeScale();
        __temp = Mathf.clamp(__temp, 0.1f, 200.0f);
        Time.setTimeScale(__temp);
    }
    public void resetTimescale() {
        Time.setTimeScale(1.0f);
    }
    public void exit() {
        Debug.log("TEST");
        instance().stop();
    }
}