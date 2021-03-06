package nhl.containing.simulator.world;

import nhl.containing.simulator.game.PlatformStorage;
import nhl.containing.simulator.framework.Behaviour;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.framework.Point3;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;
import nhl.containing.simulator.game.AGV;
import nhl.containing.simulator.game.Container;
import nhl.containing.simulator.game.PlatformInland;
import nhl.containing.simulator.game.PlatformLorry;
import nhl.containing.simulator.game.PlatformSea;
import nhl.containing.simulator.game.PlatformTrain;
import nhl.containing.simulator.framework.Point2;
import nhl.containing.simulator.framework.Tuple;
import nhl.containing.simulator.framework.Utilities;
import nhl.containing.simulator.game.AgvPath;
import nhl.containing.simulator.game.Crane;
import nhl.containing.simulator.game.RFID;
import nhl.containing.simulator.game.Train;
import nhl.containing.simulator.game.Vehicle;

/**
 * The creator and updater of the world
 * and all its components
 * (platforms, vehicles, terrain)
 * 
 * @author sietse
 */
public class World extends Behaviour {
    public static final boolean USE_DIFFUSE = true;
    public static final Point2 STORAGE_SIZE = new Point2(45, 72); // x = containers length per storage; y = storage amount
    
    public static final float WORLD_HEIGHT =  0.0f;
    public static final float WORLD_DEPTH = -150.0f;
    public static final float WATER_LEVEL = -5.0f;
    public static final float LAND_HEIGHT_EXTEND = 100.0f;
    
    public static final int AGV_COUNT = 100;//100;
    public static final float LANE_WIDTH = 10.0f;
    public static final int LANE_COUNT = 4;
    
    public static final float STORAGE_LENGTH = 1550.0f;// - LANE_WIDTH * LANE_COUNT;
    public static final float STORAGE_WIDTH = 600.0f;
    
    public static final float EXTENDS = 100.0f;
    public static final int SEA_SHIP_CRANE_COUNT = 10;
    public static final int SEA_SHIP_COUNT = 2;
    public static final int TRAIN_CRANE_COUNT = 4;
    public static final int LORRY_CRANE_COUNT = 20;
    public static final int INLAND_SHIP_CRANE_COUNT = 8;
    public static final int INLAND_SHIP_COUNT = 2;
    
    
    public static final int STORAGE_BEGIN = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT + SEA_SHIP_CRANE_COUNT;
    public static final int SEASHIP_BEGIN = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT;
    public static final int LORRY_BEGIN = INLAND_SHIP_CRANE_COUNT;
    public static final int TRAIN_BEGIN = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT + SEA_SHIP_CRANE_COUNT + STORAGE_SIZE.y;
    
    public static final float TRAIN_CRANE_DISTANCE = 10.0f;
    
    public static Vector3f containerSize() {
        return new Vector3f(2.4375f, 2.591f, 12.192f);
    }
    
    // Main
    private DirectionalLight m_sun;
    private int m_waitFrames = 0;
    private final int FRAMES_TO_WAIT = 2;
    
    // World
    private List<PlatformInland > m_inlandCells  = new ArrayList<>(0);
    private List<PlatformSea    > m_seaCells     = new ArrayList<>(0);
    private List<PlatformStorage> m_storageCells = new ArrayList<>(0);
    private List<Tuple<PlatformTrain, Vector2f>> m_trainCells = new ArrayList<>(0);
    private List<Tuple<PlatformLorry, Vehicle>> m_lorryCells = new ArrayList<>(0);
    
    // Vehicles
    private Train m_train;
    private List<Tuple<Vehicle, Vector3f>> m_seaShips = new ArrayList<>(); // Vehicle, Inland parking position
    private List<Tuple<Vehicle, Vector3f>> m_inlandShips = new ArrayList<>(); // Vehicle, Inland parking position
    private List<Tuple<Integer, Container>> m_containersFromTrain = null;
    private List<Container> m_containersToTrain = null;
    
    @Override
    public void awake() {
        m_sun = LightCreator.createSun(ColorRGBA.White, new Vector3f(-0.5f, -0.5f, -0.5f));
        LightCreator.createAmbient(new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
        Main.camera().createShadowsFiler(m_sun);
    }
    
    @Override
    public void start() {
        createSky();
        createSeap();
        createGround();
        createInland();
        createLorryCell();
        createSea();
        createStorageCell();
        createTrainCell();
        createAGV();
        Main.getSimClient().Start();

        //test();
    }
    /**
     * nested class so the platforms can be asynchronously updated
     */
    class InlandUpdater implements Runnable
    {
        PlatformInland s;
        public InlandUpdater(PlatformInland s)
        {
            this.s = s;
        }
        @Override
        public void run()
        {
            s.update();
        }
    }
    /**
     * nested class so the platforms can be asynchronously updated
     */
    class SeaUpdater implements Runnable
    {
        PlatformSea s;
        public SeaUpdater(PlatformSea s)
        {
            this.s = s;
        }
        @Override
        public void run()
        {
            s.update();
        }
    }
    /**
     * nested class so the platforms can be asynchronously updated
     */
    class StorageUpdater implements Runnable
    {
        PlatformStorage s;
        public StorageUpdater(PlatformStorage s)
        {
            this.s = s;
        }
        @Override
        public void run()
        {
            s.update();
        }
    }
    @Override
    public void update() {
        if (m_waitFrames++ < FRAMES_TO_WAIT)
            return;
        Queue<Future<?>> queue = new LinkedList<>();
        for(PlatformInland  s : m_inlandCells  ) queue.add(Main.executorService().submit(new InlandUpdater(s)));// s.update();
        for(PlatformSea     s : m_seaCells     ) queue.add(Main.executorService().submit(new SeaUpdater(s)));//s.update();
        for(PlatformStorage s : m_storageCells ) queue.add(Main.executorService().submit(new StorageUpdater(s)));//s.update();
        while (!queue.isEmpty())
        {
            try
            { 
                queue.poll().get();
            }
            catch (Exception ex) { }
 }
        for(Tuple<PlatformLorry, Vehicle> s : m_lorryCells) {
            Vehicle.VehicleState st = s.b.state();
            
            s.a.update();
            s.b.update();
            
            if (st != s.b.state()) {
                if (s.b.state() == Vehicle.VehicleState.Waiting) {
                    if (s.b.getContainer() != null && s.b.getContainer().transform != null) {
                        Container c = s.b.setContainer(null);
                        s.a.setContainer(c);
                        s.a.take(Point3.zero(), 0);
                    } else {
                        // Probably only here to get a container.
                    }
                }
            } else if (s.b.state() == Vehicle.VehicleState.Waiting) {
                // check if can go away
                
//                if (s.a.crane().isUp() /*&& !s.b.needsContainer*/) {
//                    if (!s.) {
//                        s.b.state(Vehicle.VehicleState.ToOut);
//                    } else if (true /*s.b.neededContainer() == s.b.getContainer()*/) {
//                        s.b.state(Vehicle.VehicleState.ToOut);
//                    }
//                }
            }
        }
        trainUpdate();
        updateInland();
        updateSea();
        
        //utest();
    }
    
    private void createAGV() {
        
        for(int i = 0; i < AGV_COUNT; i++){
            AGV agv = new AGV();
            agv.setContainer(null);
            agv.position(new Vector3f(-10f, -645f, -36.0f));
        }
        //m_storageCells.get(0).getParkingSpot(0).agv(agv);
    }
    
    private void test()
    {
        for (int i = 0; i < 3; i++) {
            m_storageCells.get(0).take(new Point3(4, 4, i), 0);
        }
        //m_storageCells.get(0).place(0, new Point3(4, 5, 1));
        
        for (int i = 0; i < m_lorryCells.size(); i++) {
            m_lorryCells.get(i).b.state(Vehicle.VehicleState.ToLoad);
            Container c = new Container(new RFID());
            ContainerPool.get(c);
            m_lorryCells.get(i).b.setContainer(c);
            m_lorryCells.get(i).a.getParkingSpot(0).agv(null);
        }
        
        //m_train.state(Vehicle.VehicleState.ToLoad);
        //m_train.init(30);
        //m_train.init(10);
        
        //
        m_seaShips.get(0).a.state(Vehicle.VehicleState.ToLoad);
        m_seaShips.get(0).a.init(1000);
        //m_seaShip.state(Vehicle.VehicleState.ToLoad);
    }
    
    //Vehicle.VehicleState __testst = Vehicle.VehicleState.ToLoad;
    private void utest() {
        //if (m_seaShips.get(0).a.state() != __testst) {
            
           // m_seaCells.get(0).take(Point3.zero());
           // __testst = m_seaShips.get(0).a.state();
        //}
    }
    
    private void updateInland() {
        for (int i = 0; i < m_inlandShips.size(); i++) {
            m_inlandShips.get(i).a.update();
        }
        for (int i = 0; i < m_inlandCells.size(); i++) {
            m_inlandCells.get(i).update();
        }
    }
    private void createInland() {
        m_inlandShips = new ArrayList<>();
        Vector3f bPos = new Vector3f(-150f, WORLD_HEIGHT -2f, STORAGE_WIDTH + EXTENDS + 200f);
        float bOff = 200.0f;
        for (int i = 0; i < INLAND_SHIP_COUNT; i++) {
            Tuple<Vehicle, Vector3f> t = new Tuple<>(null, new Vector3f(bPos));
            createInlandCell(t);
            bPos.x -= bOff;
            m_inlandShips.add(t);
        }
    }
    private void createInlandCell(Tuple<Vehicle, Vector3f> v) {
        //Vector3f _dest = new Vector3f(-500f, WORLD_HEIGHT -2f, STORAGE_WIDTH + EXTENDS + 200f);
        Vector3f _dest = new Vector3f(-200f, WORLD_HEIGHT -2f, STORAGE_WIDTH + EXTENDS + 200f);
        v.a = WorldCreator.createInland(
            new Vector3f[] {
                new Vector3f(_dest),
                new Vector3f(v.b)
            }, 
            new Vector3f[] {
                new Vector3f(v.b),
                new Vector3f(-_dest.x, _dest.y, _dest.z)
            }
        );
        Vector3f offset;
        if(m_inlandCells.isEmpty())
            offset = new Vector3f(-150f, WORLD_HEIGHT, STORAGE_WIDTH + EXTENDS + 110);
        else
            offset = new Vector3f(-750f, WORLD_HEIGHT, STORAGE_WIDTH + EXTENDS + 110);
        int begin = m_inlandCells.size();
        
        for (int i = 0; i < INLAND_SHIP_CRANE_COUNT / INLAND_SHIP_COUNT; ++i) {
            m_inlandCells.add(new PlatformInland(offset,i + begin, v.a));
            m_inlandCells.get(m_inlandCells.size() - 1).initSpots(Point3.one());
            offset.x -= 150.0f;
        }
    }
    
    private void createLorryCell() {
        Vector3f offset = new Vector3f(STORAGE_LENGTH, WORLD_HEIGHT, STORAGE_WIDTH + EXTENDS + 70.0f);
        for (int i = 0; i < LORRY_CRANE_COUNT; ++i) {
            Tuple<PlatformLorry, Vehicle> _temp = new Tuple<>();
            _temp.a = new PlatformLorry(offset,i + INLAND_SHIP_CRANE_COUNT);
            
            Vector3f _from = new Vector3f(offset);
            _from = _from.add(new Vector3f(0.0f, 0.0f, 140.0f)); // Base offset
            Vector3f _to = new Vector3f(offset).add(new Vector3f(0.0f, 0.0f, 40.0f));
            
            _to.z += 30.0f;
            _temp.b = WorldCreator.createLorry(_from, _to);
            
            m_lorryCells.add(_temp);
            offset.x -= STORAGE_LENGTH / LORRY_CRANE_COUNT;
        }
    }
    
    private void updateSea() {
        for (int i = 0; i < m_seaShips.size(); i++) {
            m_seaShips.get(i).a.update();
        }
        for (int i = 0; i < m_seaCells.size(); i++) {
            m_seaCells.get(i).update();
        }
    }
    private void createSea() {
        m_seaShips = new ArrayList<>();
        Vector3f bPos = new Vector3f(-STORAGE_LENGTH - 400, WORLD_HEIGHT, STORAGE_WIDTH + EXTENDS - 400);
        float bOff = 200.0f;
        
        for (int i = 0; i < SEA_SHIP_COUNT; i++) {
            Tuple<Vehicle, Vector3f> t = new Tuple<>(null, new Vector3f(bPos));
            createSeaCell(t);
            bPos.x += bOff;
            m_seaShips.add(t);
        }
        
        
    }
    private void createSeaCell(Tuple<Vehicle, Vector3f> v) {
        v.a = WorldCreator.createSea(
                new Vector3f[]{
                    //new Vector3f(-STORAGE_LENGTH - 1000.0f, 0.0f, 3000.0f),
                    //new Vector3f(-STORAGE_LENGTH - 500.0f, 0.0f, 1000.0f),
                    new Vector3f(v.b)
                },
                new Vector3f[] {
                    new Vector3f(v.b),
                    //new Vector3f(-STORAGE_LENGTH - 500.0f, 0.0f, -1000.0f),
                    //new Vector3f(-STORAGE_LENGTH - 1000.0f, 0.0f, -3000.0f),
                }
                );
        Vector3f offset;
        if(m_seaCells.isEmpty())
            offset = new Vector3f(-STORAGE_LENGTH - 280, WORLD_HEIGHT, STORAGE_WIDTH + EXTENDS - 200);
        else
            offset = new Vector3f(-STORAGE_LENGTH - 280, WORLD_HEIGHT , STORAGE_WIDTH + EXTENDS - 775);
        int begin = m_seaCells.size() + INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT;
        
        for (int i = 0; i < SEA_SHIP_CRANE_COUNT / SEA_SHIP_COUNT; ++i) {
            PlatformSea sea = new PlatformSea(offset,i + begin,v.a);
            sea.initSpots(new Point3(1, 1, 1));
            sea.rotate(0, 90, 0);
            sea.crane().m_frameSpatial.setLocalRotation(Utilities.euler2Quaternion(new Vector3f(0.0f, -90.0f, 0.0f)));
            sea.crane().m_hookSpatial.setLocalRotation(Utilities.euler2Quaternion(new Vector3f(0.0f, -90.0f, 0.0f)));
            sea.crane().hookMovementAxis = Crane.Y_AXIS | Crane.Z_AXIS;
            m_seaCells.add(sea);
            offset.z -= 100.0f;
        }
    }
    
    private void createStorageCell() {
        Vector3f offset = new Vector3f(-LANE_WIDTH / 2 - STORAGE_LENGTH, WORLD_HEIGHT, -STORAGE_WIDTH + 50.0f);
        int begin = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT + SEA_SHIP_CRANE_COUNT;
        
        
        final float _17 = AgvPath.getNodes()[17].position().x;
        final float _18 = AgvPath.getNodes()[18].position().x;
        final float _19 = AgvPath.getNodes()[19].position().x;
        
        final float _21 = AgvPath.getNodes()[21].position().x;
        final float _22 = AgvPath.getNodes()[22].position().x;
        final float _23 = AgvPath.getNodes()[23].position().x;
        
        for (int i = 0; i < STORAGE_SIZE.y; ++i) {
            
            m_storageCells.add(new PlatformStorage(offset,i + begin));
            
            int upArriveID = -1;
            int upDepartID = -1;
            int downArriveID = -1;
            int downDepartID = -1;
            if (offset.x >= _17) {
                upArriveID = 16;
                upDepartID = 17;
                downArriveID = 29;
                downDepartID = 28;
            } else if (offset.x >= _18) {
                upArriveID = 17;
                upDepartID = 18;
                downArriveID = 30;
                downDepartID = 29;
            } else if (offset.x >= _19) {
                upArriveID = 18;
                upDepartID = 19;
                downArriveID = 30;
                downDepartID = 29;
            } else if (offset.x >= _21) {
                upArriveID = 20;
                upDepartID = 21;
                downArriveID = 32;
                downDepartID = 31;
            } else if (offset.x >= _22) {
                upArriveID = 21;
                upDepartID = 22;
                downArriveID = 32;
                downDepartID = 31;
            } else if (offset.x >= _23) {
                upArriveID = 22;
                upDepartID = 23;
                downArriveID = 33;
                downDepartID = 32;
            } else {
                // ERROR
                System.out.println("ERROR: ");
            }
            
            final int l = m_storageCells.get(i).parkingSpotLength();
            final int hl = l / 2; // Not half life
            for (int j = 0; j < l; j++) {
                
                if (j >= hl) { // Up
                    m_storageCells.get(i).getParkingSpot(j).arrivalID(upArriveID);
                    m_storageCells.get(i).getParkingSpot(j).departID(upDepartID);
                    continue;
                }
                
                // Down
                m_storageCells.get(i).getParkingSpot(j).arrivalID(downArriveID);
                m_storageCells.get(i).getParkingSpot(j).departID(downDepartID);
            }
            
            
            if (i == 35) // Adding space for the middle road
                offset.x += LANE_WIDTH * LANE_COUNT * 2 + 7.5f;
            offset.x += containerSize().x * 6 + 27.5f;
        }
    }
    
    public Train getTrain() {
        return m_train;
    }
    /**
     * Get train platforms
     * @return list with train platforms
     */
    public List<Tuple<PlatformTrain,Vector2f>> getTrainPlatforms(){
        return m_trainCells;
    }
    /**
     * Get inland platforms
     * @return list with inland platforms
     */
    public List<PlatformInland> getInlandPlatforms(){
        return m_inlandCells;
    }
    
    /**
     * get lorry platforms
     * @return list with lorry platforms
     */
    public List<Tuple<PlatformLorry,Vehicle>> getLorryPlatforms(){
        return m_lorryCells;
    }
    
    public List<PlatformSea> getSeaPlatforms(){
        return m_seaCells;
    }
    
    public List<PlatformStorage> getStoragePlatforms(){
        return m_storageCells;
    }
    
    public void trainArrived() {
        m_containersFromTrain = new ArrayList<>(0);
        for (int i = 0; i < m_train.size().z; i++) {
            m_containersFromTrain.add(new Tuple(i, m_train.getContainer(0, 0, i)));
        }
    }
    
    /**
     * Sends a train take command
     * @param s train platform
     * @param x point
     */
    public void sendTrainTake(Tuple<PlatformTrain, Vector2f> s, int x){
        s.a.update();
            s.b.y = s.a.position().z;    
            if(m_train.state() == Vehicle.VehicleState.Waiting) {
                if (s.a.crane().getContainer() != null) 
                    return;
                //TEMPFIX
                Container c = m_train.setContainer(new Point3(0,0,x), null);
                if(c == null)
                    p("oops.. container is null");
                //Vector3f pos = c.transform.position(); //<-- container is null??
                //Quaternion rot = c.transform.rotation();
                  
                s.a.setContainer(Point3.zero(), c);
                s.a.take(Point3.zero(), 0);
                
            }
    }
    
    public void sendInlandTake(PlatformInland inland, Point3 point,int index){
        Vehicle inlandShip = m_inlandShips.get(index).a;
        if(inlandShip.state() == Vehicle.VehicleState.Waiting){
            if(inland.crane().getContainer() != null)
                return;
            Container c = inlandShip.setContainer(point,null);
            inland.setContainer(Point3.zero(), c);
            inland.take(Point3.zero(), 0);
        }
    }
    
    /**
     * Sends a storage place command
     * @param storage
     * @param index
     * @param point 
     */
    public void sendStoragePlace(PlatformStorage storage,int index, Point3 point){
        Container c = storage.getParkingSpot(index).future_agv.getContainer();
        storage.place(index, point);
    }
    
    
    public void trainUpdate() {
        m_train.update();
        for(Tuple<PlatformTrain, Vector2f> s : m_trainCells   ) {
            s.a.update();
            s.b.y = s.a.position().z;
        }
    }
    private int getTrainContainerTarget() {
        return (m_train.getContainer() == null) ? -1 : 0;
    }
    
    
    public Vehicle getSeaShip(int index) {
        return m_seaShips.get(index).a;
    }

    public Vehicle getInlandShip(int index) {
        return m_inlandShips.get(index).a;
    }
    
    private void createTrainCell() {
        Vector3f offset = new Vector3f(-100.0f, WORLD_HEIGHT, -730.0f);
        int begin = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT + SEA_SHIP_CRANE_COUNT + STORAGE_SIZE.y;
        for (int i = 0; i < TRAIN_CRANE_COUNT; ++i) {
            Tuple<PlatformTrain,Vector2f> train = new Tuple(new PlatformTrain(offset,i + begin), new Vector2f(10.0f, 0.0f));
            train.a.rotate(0.0f, -90.0f, 0.0f);
            m_trainCells.add(train);
            offset.x += 150.0f;
        }
        
        final float zOff = -STORAGE_WIDTH - EXTENDS - LANE_WIDTH * LANE_COUNT;
        m_train = WorldCreator.createTrain(
                new Vector3f(2600.0f, 10.0f,  zOff),
                new Vector3f(-200.0f, 10.0f, zOff));
        
        m_train.rotate(0.0f, -90.0f, 0.0f);
    }
    
    private void createSky()
    {
        Main.instance().getRootNode().attachChild(SkyFactory.createSky(
        Main.instance().getAssetManager(), "Textures/BrightSky.dds", false));
    }
    
    private void createSeap()
    {
        Geometry waterplane = WorldCreator.createWaterPlane(new Vector3f(-8000,WATER_LEVEL,8000), 16000, 40, 0.05f, 0.05f, 6f);
        Main.instance().getRootNode().attachChild(waterplane);
    }
    
    private void createGround() {
        createStorageGround();
        createRoadGround();
        createLorryGround();
        createInlandGround();
        createShippingGround();
        createTrainGround();
        createOtherGround();
    }
    private void createStorageGround() {
        // Storage
        Geometry storageEast = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH / 2.0f, 1.0f, STORAGE_WIDTH),       // Size
                new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        storageEast.setLocalTranslation((LANE_WIDTH * LANE_COUNT) + STORAGE_LENGTH / 2.0f, WORLD_HEIGHT-1.0f, 0.0f);
        
        // 
        Geometry storageWest = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH / 2.0f, 1.0f, STORAGE_WIDTH),       // Size
                new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        storageWest.setLocalTranslation(-(LANE_WIDTH * LANE_COUNT) - STORAGE_LENGTH / 2.0f, WORLD_HEIGHT-1.0f, 0.0f);
    }
    private void createRoadGround() {
        final ColorRGBA roadColor = new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f);
        
        // Storage
        Geometry middleRoad = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(LANE_WIDTH * LANE_COUNT, 1.0f, STORAGE_WIDTH),       // Size
                roadColor,                          // Color
                true, false                                                     // Other
        );
        middleRoad.setLocalTranslation(0.0f, WORLD_HEIGHT-1.0f, 0.0f);
        
        // Storage
        Geometry westRoad = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(LANE_WIDTH * LANE_COUNT, 1.0f, STORAGE_WIDTH),       // Size
                roadColor,                          // Color
                true, false                                                     // Other
        );
        westRoad.setLocalTranslation(-STORAGE_LENGTH - LANE_WIDTH * 2 * LANE_COUNT, WORLD_HEIGHT-1.0f, 0.0f);
        
        // Storage
        Geometry eastRoad = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(LANE_WIDTH * LANE_COUNT, 1.0f, STORAGE_WIDTH),       // Size
                roadColor,                          // Color
                true, false                                                     // Other
        );
        eastRoad.setLocalTranslation(STORAGE_LENGTH + LANE_WIDTH * 2 * LANE_COUNT, WORLD_HEIGHT-1.0f, 0.0f);
        
        // Storage
        Geometry northRoad = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH + LANE_WIDTH * LANE_COUNT * 3, 1.0f, LANE_WIDTH * LANE_COUNT),       // Size
                roadColor,                          // Color
                true, false                                                     // Other
        );
        northRoad.setLocalTranslation(0, WORLD_HEIGHT-1.0f, STORAGE_WIDTH + LANE_WIDTH * LANE_COUNT);
        
        // Storage
        Geometry southRoad = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH + LANE_WIDTH * LANE_COUNT * 3, 1.0f, LANE_WIDTH * LANE_COUNT),       // Size
                roadColor,                          // Color
                true, false                                                     // Other
        );
        southRoad.setLocalTranslation(0, WORLD_HEIGHT-1.0f, -STORAGE_WIDTH - LANE_WIDTH * LANE_COUNT);
    }
    private void createLorryGround() {
        // Storage
        Geometry lorryGround = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH / 2 + LANE_WIDTH * LANE_COUNT, 1.0f, EXTENDS),       // Size
                new ColorRGBA(0.5f, 0.6f, 0.8f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        lorryGround.setLocalTranslation(STORAGE_LENGTH / 2 + LANE_WIDTH * LANE_COUNT * 2, WORLD_HEIGHT-1.0f, STORAGE_WIDTH + 2 * LANE_WIDTH * LANE_COUNT + EXTENDS);
    }
    private void createInlandGround() {
        // Storage
        Geometry lorryGround = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH / 2 + LANE_WIDTH * LANE_COUNT * 2, 1.0f, EXTENDS),       // Size
                new ColorRGBA(0.6f, 0.8f, 0.5f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        lorryGround.setLocalTranslation(-STORAGE_LENGTH / 2 - LANE_WIDTH * LANE_COUNT , WORLD_HEIGHT-1.0f, STORAGE_WIDTH + 2 * LANE_WIDTH * LANE_COUNT + EXTENDS);
    }
    private void createShippingGround() {
        // Storage
        Geometry lorryGround = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(EXTENDS, 1.0f, STORAGE_WIDTH + LANE_WIDTH * LANE_COUNT * 2 + EXTENDS * 2),       // Size
                new ColorRGBA(0.8f, 0.6f, 0.5f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        lorryGround.setLocalTranslation(-STORAGE_LENGTH - LANE_WIDTH * LANE_COUNT * 3 - EXTENDS, WORLD_HEIGHT-1.0f, 0.0f);
    }
    private void createTrainGround() {
        // Storage
        Geometry lorryGround = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(STORAGE_LENGTH + LANE_WIDTH * LANE_COUNT * 3, 1.0f, EXTENDS),       // Size
                new ColorRGBA(0.6f, 0.8f, 0.5f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        lorryGround.setLocalTranslation(0.0f, WORLD_HEIGHT-1.0f, -STORAGE_WIDTH - 2 * LANE_WIDTH * LANE_COUNT - EXTENDS);
    }
    private void createOtherGround() {
        Geometry belowMainGround = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(
                    STORAGE_LENGTH + LANE_WIDTH * LANE_COUNT * 3 + EXTENDS, 
                    -WORLD_DEPTH, 
                    STORAGE_WIDTH + LANE_WIDTH * LANE_COUNT * 2 + EXTENDS * 2),       // Size
                new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f),                          // Color
                false, false                                                     // Other
        );
        belowMainGround.setLocalTranslation(-EXTENDS, WORLD_HEIGHT - 1.5f + WORLD_DEPTH, 0.0f);
        
        Geometry aBlock = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(
                    1.0f, 
                    LAND_HEIGHT_EXTEND, 
                    LANE_WIDTH * LANE_COUNT * 2 + STORAGE_WIDTH + EXTENDS * 2.0f),       // Size
                new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        aBlock.setLocalTranslation(STORAGE_LENGTH + LANE_WIDTH * LANE_COUNT * 3, LAND_HEIGHT_EXTEND + WORLD_HEIGHT - 0.5f, 0.0f);
        
        Geometry bBlock = WorldCreator.createBox(
                null,                                                           // Parent
                new Vector3f(
                    STORAGE_LENGTH / 2 + LANE_WIDTH * LANE_COUNT, 
                    LAND_HEIGHT_EXTEND, 
                    1.0f),       // Size
                new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f),                          // Color
                true, false                                                     // Other
        );
        bBlock.setLocalTranslation(STORAGE_LENGTH / 2 + LANE_WIDTH * LANE_COUNT * 2, LAND_HEIGHT_EXTEND + WORLD_HEIGHT - 0.5f, STORAGE_WIDTH + 2 * LANE_WIDTH * LANE_COUNT + EXTENDS * 2.0f);
    }
    
    private static void p(String s)
    {
        System.out.println("[" + System.currentTimeMillis() + "] Sim: " + s);
    }
}
