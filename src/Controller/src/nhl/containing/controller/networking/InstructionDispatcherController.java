package nhl.containing.controller.networking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import nhl.containing.controller.PathFinder;
import nhl.containing.controller.Point3;
import nhl.containing.controller.Simulator;
import nhl.containing.controller.simulation.*;
import nhl.containing.networking.protobuf.*;
import nhl.containing.networking.protocol.*;

/** InstructionDispatcherController
 * Handles the instructions recieved over the network
 * @author Jens
 */
public class InstructionDispatcherController implements InstructionDispatcher {

    Simulator _sim;
    SimulatorItems _items;
    SimulationContext _context;
    CommunicationProtocol _com;
    private ExecutorService executorService;
    private Queue<Future> futures;
    public List<SavedInstruction> m_agvInstructions = new ArrayList<>();

    public InstructionDispatcherController(Simulator sim, CommunicationProtocol com) {
        _sim = sim;
        _com = com;
        _items = _sim.getController().getItems();
        _context = _sim.getController().getContext();
        executorService = Executors.newSingleThreadExecutor();
        futures = new LinkedList<>();
    }

    /**
     * dispatchInstruction(instruction) checks the instructiontype and forwards
     * the instruction to the appropriate component in the Contoller
     *
     * @param inst The Instruction to be dispatched to the system
     */
    @Override
    public void forwardInstruction(final InstructionProto.Instruction inst) {

        switch (inst.getInstructionType()) {
            case InstructionType.CONSOLE_COMMAND:
                String message = inst.getMessage();
                System.out.println("GOT CONSOLECOMAND: " + message);
                //rdataBuilder.setMessage(_sim.parseCommand(message));
                break;
            case InstructionType.CLIENT_TIME_UPDATE:
                //futures.add(executorService.submit(new Tickhandler(inst)));
                new Tickhandler(inst).run();
                break;
            case InstructionType.SHIPMENT_ARRIVED:
                shipmentArrived(inst);
                break;
            case InstructionType.CRANE_TO_AGV_READY:
                craneToAGVReady(inst);
                break;
            case InstructionType.AGV_READY:
                agvReady(inst);
                break;
            case InstructionType.PLACE_CRANE_READY:
                placeCraneReady(inst);
                break;
            case InstructionType.CRANE_TO_STORAGE_READY:
                craneToStorageReady(inst);
                break;
            case InstructionType.DEPARTMENT_ARRIVED:
                departmentArrived(inst);
                break;
            //More instruction types here..
        }
    }
    
    /**
     * Handles crane to storage ready instruction
     * @param instruction instruction
     */
    private void craneToStorageReady(InstructionProto.Instruction instruction){
        Platform platform = null;
        
        if(instruction.getA() < SimulatorItems.LORRY_BEGIN){
            //dit is een inlandship platform
            platform = _items.getInlandPlatforms()[instruction.getA()];
        }else if(instruction.getA() < SimulatorItems.SEASHIP_BEGIN){
            //dit is een lorry platform
            platform = _items.getLorryPlatforms()[instruction.getA() - SimulatorItems.LORRY_BEGIN];
            LorryPlatform lp = (LorryPlatform) platform;
            
            shipmentMoved(lp.getShipment());
        } else if (instruction.getA() < SimulatorItems.STORAGE_BEGIN) {
            //dit is een seaship platform
            platform = _items.getSeaShipPlatforms()[instruction.getA() - SimulatorItems.SEASHIP_BEGIN];
        }else if(instruction.getA() < SimulatorItems.TRAIN_BEGIN){
            //dit is een storage platform
            platform =  _items.getStorages()[instruction.getA() - SimulatorItems.STORAGE_BEGIN];
        } else {
            //dit is een train platform
            platform = _items.getTrainPlatforms()[instruction.getA() - SimulatorItems.TRAIN_BEGIN];

        }
        
        platform.unsetBusy();
        Parkingspot spot = platform.getParkingspots().get(instruction.getB());
        AGV agv = spot.getAGV();
        agv.getContainer().currentCategory = AppDataProto.ContainerCategory.REMAINDER;
        agv.unsetContainer();
        spot.removeAGV();
        agv.stop(); //set the agv to not busy so it can take a new job in the tickhandler
    }

    /**
     * Handles shipment arrived
     *
     * @param instruction instruction
     */
    private void shipmentArrived(InstructionProto.Instruction instruction)
    {
        if (_items == null)
        {
            _items = _sim.getController().getItems();
        }

        Shipment shipment = _context.getShipmentByKey(instruction.getMessage());
        if (shipment == null)
        { //TODO: handle error
            return;
        }

        //Assign a storage platform to this batch of incomming containers.
        _context.determineContainerPlatforms(shipment.carrier.containers);

        shipment.arrived = true;
        //TODO: if truck shipment, check platform id
        Platform[] platformsByCarrier;
        if(shipment.carrier instanceof Truck){
            LorryPlatform lp = _items.getLorryPlatforms()[instruction.getA() - SimulatorItems.LORRY_BEGIN];
            lp.containers = new ArrayList<>(shipment.carrier.containers);
            m_agvInstructions.add(new SavedInstruction(null, lp, lp.getParkingspots().get(0)));
            placeCrane(lp);
            return;
        }else if(shipment.carrier instanceof SeaShip){
            //Get right platforms
            platformsByCarrier = _items.getSeaPlatformsByShipment(shipment);
        }else if(shipment.carrier instanceof InlandShip){
            //Get right platforms
            platformsByCarrier = _items.getInlandPlatformsByShipment(shipment);
        }else{
            platformsByCarrier = _items.getPlatformsByCarrier(shipment.carrier);
        }
        // Get the platforms and containers.
        final List<ShippingContainer> allContainers = shipment.carrier.containers;
        // Determine how many containers per crane.
        int split = allContainers.size() / platformsByCarrier.length;
        int take = split;

        // Loop variables/
        int i = 0;
        int skip = 0;
        Collections.reverse(allContainers);
        for (Platform platform : platformsByCarrier)
        {
            if (platform.isBusy())
            {
                continue;
            }

            // Get a subset of the containers which get handled by this crane.
            // We create a copy of the list so the containers don't get removed from the source list.
            List<ShippingContainer> containers = new ArrayList<>(allContainers.subList(skip, take));

            // This is the last crane, add the remaining containers as well.
            if (i == platformsByCarrier.length - 1)
            {
                containers.addAll(allContainers.subList(take, allContainers.size()));
            }
            //Collections.reverse(containers);
            
            // Assign the containers to the platform.
            platform.containers = containers;
            placeCrane(platform);

            // Increase loop variables.
            skip += split;
            take += split;
            i++;
        }
        
    }

    /**
     * Places a crane for the sea/inland storage and train platform
     *
     * @param platform platform
     */
    private void placeCrane(Platform platform) { placeCrane(platform, null, 0);}
    
    /**
     * Places a crane for the sea/inland storage and train platform
     * @param platform platform
     * @param containerPos container position
     * @param parkingSpot parkingspot
     */
    private void placeCrane(Platform platform, Point3 containerPos, long parkingSpot) {
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(platform.getID());
        ShippingContainer container = null;
        if(containerPos == null)
        {
            container = platform.containers.get(0);
            containerPos = container.position;
        }
        platform.containers.remove(0);
        builder.setB((int)parkingSpot);
        if(platform.getID() >= SimulatorItems.SEASHIP_BEGIN && platform.getID() < SimulatorItems.STORAGE_BEGIN){
            if(containerPos.x > 19 || containerPos.y > 15 || containerPos.z > 5){
                placeCrane(platform);
                return;
            }
            builder.setX(containerPos.x);
            builder.setY(containerPos.z);
            builder.setZ(containerPos.y);
        }else{
            builder.setX(containerPos.x);
            builder.setY(containerPos.y);
            builder.setZ(containerPos.z);
        }
        builder.setInstructionType(InstructionType.PLACE_CRANE);
        _com.sendInstruction(builder.build());
        platform.setBusy();
    }

    /**
     * Handles place crane ready instruction
     *
     * @param instruction
     */
    private void placeCraneReady(InstructionProto.Instruction instruction) {
        Platform platform = null;
        Parkingspot ps = _items.getParkingspotByID(instruction.getB());
        if(instruction.getA() < SimulatorItems.LORRY_BEGIN){
            //dit is een inlandship platform
            platform = _items.getInlandPlatforms()[instruction.getA()];
        }else if(instruction.getA() < SimulatorItems.SEASHIP_BEGIN){
            //dit is een lorry platform
            //do nothing
        } else if (instruction.getA() < SimulatorItems.STORAGE_BEGIN) {
            //dit is een seaship platform
            platform = _items.getSeaShipPlatforms()[instruction.getA() - SimulatorItems.SEASHIP_BEGIN];
        }else if(instruction.getA() < SimulatorItems.TRAIN_BEGIN){
            //dit is een storage platform
            //Stuur hier de agv naar het department platform..
            //platform = _context.parkingspot_Containertopickup.get(ps).departureShipment
            //platform = null; //<- het is hier voor nu even de bedoeling dat hij een exception throwed.
            //System.out.println("Should send the AGV to departure");
        } else {
            //dit is een train platform
            platform = _items.getTrainPlatforms()[instruction.getA() - SimulatorItems.TRAIN_BEGIN];

        }
        m_agvInstructions.add(new SavedInstruction(null, platform, ps));

    }

    /**
     * Sends Move AGV command
     *
     * @param agv the AGV
     * @param to the destination platform
     * @param spot the spot where the agv needs to go
     */
    public void moveAGV(AGV agv, Platform to, Parkingspot spot) {
        if(agv != null){
            InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
            builder.setId(CommunicationProtocol.newUUID());
            builder.setB((int)spot.getId());
            builder.setInstructionType(InstructionType.MOVE_AGV);
            builder.setA(agv.getID());
            int[] route = PathFinder.getPath(agv.getNodeID(), spot.getArrivalNodeID());
            for(int r : route){
                builder.addRoute(r);
            }
            
            try {
                agv.setBusy();
                spot.setAGV(agv);
                agv.setNodeID(spot.getDepartNodeID());
            }catch(Exception e){e.printStackTrace();} 
            
            _com.sendInstruction(builder.build());
        }else{
            m_agvInstructions.add(new SavedInstruction(null, to, spot));
        }
    }

    /**
     * Handles shipment moved
     *
     * @param shipment shipment
     */
    private void shipmentMoved(Shipment shipment) {
        if (shipment == null) {
            return; //TODO: handle error
        }
        int index = -1;
        Platform p = null;
        if(shipment.carrier instanceof SeaShip){
            p = _items.getSeaPlatformsByShipment(shipment)[0];
            index = p.getID() - SimulatorItems.SEASHIP_BEGIN < SimulatorItems.SEA_SHIP_CRANE_COUNT / 2 ? 0 : 1;
        }else if(shipment.carrier instanceof InlandShip){
            p = _items.getInlandPlatformsByShipment(shipment)[0];
            index = p.getID() < SimulatorItems.INLAND_SHIP_CRANE_COUNT / 2 ? 0 : 1;
        }else if(shipment.carrier instanceof Train){
            p = _items.getTrainPlatforms()[0];
        }else{
            p = LorryPlatform.GetPlatformbyShipment(shipment, _items.getLorryPlatforms());
        }
        shipment.containersMoved = true;
        InstructionProto.Instruction.Builder instruction = InstructionProto.Instruction.newBuilder();
        instruction.setId(CommunicationProtocol.newUUID());
        instruction.setA(p.getID());
        if(index != -1)
            instruction.setB(index);
        instruction.setMessage(shipment.key);
        instruction.setInstructionType(InstructionType.SHIPMENT_MOVED);
        _com.sendInstruction(instruction.build());
    }

    /**
     * Handles shipment moved
     *
     * @param key key of shipment
     */
    private void shipmentMoved(String key) {
        this.shipmentMoved(_context.getShipmentByKey(key));
    }

    //TODO: finish this
    /**
     * Handles crane to AGV ready instruction
     *
     * @param instruction instruction
     */
    private void craneToAGVReady(InstructionProto.Instruction instruction) {
        Platform platform = _items.getPlatformByAGVID(instruction.getA());
        platform.unsetBusy();
        ShippingContainer container = _context.getContainerById(instruction.getB());
        Platform to = _context.getStoragePlatformByContainer(container);
        Parkingspot p = platform.getParkingspotForAGV(instruction.getA());
        boolean farside = container.departureShipment.carrier instanceof Truck || container.departureShipment.carrier instanceof Train || container.departureShipment.carrier instanceof InlandShip;
        Parkingspot toSpot = to.getFreeParkingspot(farside);
        container.currentCategory = AppDataProto.ContainerCategory.AGV;
        if (platform.getID() < SimulatorItems.LORRY_BEGIN) {
            //dit is een inlandship platform
            Platform[] platforms = _items.getInlandPlatformsByShipment(container.arrivalShipment);
            if(!platform.containers.isEmpty()){
                placeCrane(platform);
            }else if (!Platform.checkIfBusy(platforms) && Platform.checkIfShipmentDone(platforms)) {
                shipmentMoved(container.arrivalShipment);
                _items.unsetInlandShipment(container.arrivalShipment);
            }
        } else if (platform.getID() < SimulatorItems.SEASHIP_BEGIN) {
            //dit is een lorry platform
            LorryPlatform lp = (LorryPlatform) platform;
            container = lp.getShipment().carrier.containers.get(0);
            shipmentMoved(lp.getShipment());
            lp.unsetShipment();
        } else if (platform.getID() < SimulatorItems.STORAGE_BEGIN) {
            //dit is een seaship platform
            Platform[] platforms = _items.getSeaPlatformsByShipment(container.arrivalShipment);
            if(!platform.containers.isEmpty()){
                placeCrane(platform);
            }else if (!Platform.checkIfBusy(platforms) && Platform.checkIfShipmentDone(platforms)) {
                shipmentMoved(container.arrivalShipment);
                _items.unsetSeaShipment(container.arrivalShipment);
            }
        } else if (platform.getID() < SimulatorItems.TRAIN_BEGIN) {
            //dit is een storage platform
            Storage storage = (Storage) platform;
            Point3 pos = new Point3(instruction.getX(), instruction.getY(), instruction.getZ());
            
            if(container.departureShipment.carrier instanceof Truck)
            {
                for(LorryPlatform cplatform : _context.getSimulatorItems().getLorryPlatforms())
                {
                    if(cplatform.hasShipment() && cplatform.getShipment().key.equals(container.departureShipment.key))
                    {
                        to = cplatform;
                        toSpot = cplatform.getFreeParkingspot(farside);
                    }
                }
            }
            
        } else {
            //dit is een train platform
            if(!platform.containers.isEmpty()){
                placeCrane(platform);
            }else if (!Platform.checkIfBusy(_items.getTrainPlatforms()) && Platform.checkIfShipmentDone(_items.getTrainPlatforms())) {
                shipmentMoved(_items.getTrainShipment());
                _items.unsetTrainShipment();
            }
        }
        
        AGV agv = p.getAGV();
        p.removeAGV();
        try {
            agv.setContainer(container);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(toSpot == null)
        {
            System.out.println("NO PARKING SPOT WAITING..");
            
            return; //TODO: error?
        }
            
         m_agvInstructions.add(new SavedInstruction(agv, to, toSpot));
    }

    /**
     * Handles agv ready instruction
     * @param instruction instruction
     */
    private void agvReady(InstructionProto.Instruction instruction) {
        //TODO: send Container to place in department shipping
        //System.out.println("agv ready..");
        Platform platform = _items.getPlatformByAGVID(instruction.getA());
        Parkingspot p = platform.getParkingspotForAGV(instruction.getA());
        Point3 position;
        if (!p.hasAGV()) //TODO: exception handling
        {
            return;
        }
        if (p.getAGV().hasContainer()) {
            if (platform.getID() < SimulatorItems.LORRY_BEGIN) {
                int index = platform.getID() - SimulatorItems.INLAND_SHIP_CRANE_COUNT < SimulatorItems.INLAND_SHIP_CRANE_COUNT / 2 ? 0 : 1;
                if (_items.hasInlandShipment(index) && _items.getInlandShipment(index).arrived) {
                    sendCraneToDepartment(platform, p);
                }
            } else if (platform.getID() < SimulatorItems.SEASHIP_BEGIN) {
                //dit is een lorry platform
                LorryPlatform lp = (LorryPlatform) platform;
                if (lp.hasShipment() && lp.getShipment().arrived) {
                    sendCraneToDepartment(platform, p);
                }
            } else if (platform.getID() < SimulatorItems.STORAGE_BEGIN) {
                //dit is een seaship platform
                int index = platform.getID() - SimulatorItems.SEA_SHIP_CRANE_COUNT < SimulatorItems.SEA_SHIP_CRANE_COUNT / 2 ? 0 : 1;
                if (_items.hasSeaShipment(index) && _items.getSeaShipment(index).arrived) {
                    sendCraneToDepartment(platform, p);
                }
            } else if (platform.getID() < SimulatorItems.TRAIN_BEGIN) {
                //dit is een storage platform
                Storage storage = (Storage) platform;
                ShippingContainer container = p.getAGV().getContainer();
                
                boolean farside = false; //Moet hij aan de overkant (ten opzichte van 0,0,0) geplaatst worden
                farside = container.departureShipment.carrier instanceof Truck || container.departureShipment.carrier instanceof Train || container.departureShipment.carrier instanceof InlandShip;
                           
                position = _context.determineContainerPosition(container, farside);
                try {
                    storage.setContainer(container, position);
                    sendCraneToStorage(storage, p, position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (_items.hasTrainShipment() && _items.getTrainShipment().arrived) {
                    sendCraneToDepartment(platform, p);
                    
                }
            }
        }
        else
        {
            //TODO: get container from storage for department!
            if(platform.getID() >= SimulatorItems.STORAGE_BEGIN && platform.getID() < SimulatorItems.TRAIN_BEGIN)
            {
                //wanneer een agv zonder container bij een storage platform aan komt
               ShippingContainer pickup = _context.agv_Containertopickup.get(p.getAGV());
               
               if (pickup == null)
               {
                   System.err.println("No container for parking spot " + p.getId());
                   return;
               }else{
                   _context.agv_Containertopickup.remove(p.getAGV());
               }
               
               placeCrane(platform, pickup.departPosition, platform.getParkingspotIndex(p));
               System.out.println("calling placeCrane..");
                
            }
        }
    }
    
    /**
     * Sends an crane to department instruction
     * @param platform platform
     * @param parkingspot parkingspot
     */
    private void sendCraneToDepartment(Platform platform, Parkingspot parkingspot){
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        builder.setInstructionType(InstructionType.CRANE_TO_DEPARTMENT);
        builder.setA(platform.getID());

        builder.setB(0); //<- when departing parkingspot is always 0
        builder.setX(0);
        builder.setY(0);
        builder.setZ(0);
        //TODO: find department position
        
        _com.sendInstruction(builder.build());
    }
    
    /**
     * Sends instruction to place a container in the storage
     * @param storage storage
     * @param p parkingspot
     * @param position position
     */
    private void sendCraneToStorage(Storage storage, Parkingspot p, Point3 position){
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(storage.getID());
        builder.setB(storage.getParkingspotIndex(p));
        builder.setInstructionType(InstructionType.CRANE_TO_STORAGE);
        builder.setX(position.x);
        builder.setY(position.y);
        builder.setZ(position.z);
        _com.sendInstruction(builder.build());
    }
    
private void departmentArrived(InstructionProto.Instruction instruction) {
     Platform platform = null;
    if(instruction.getA() < SimulatorItems.LORRY_BEGIN){
        //dit is een inlandship platform
        platform = _items.getInlandPlatforms()[instruction.getA()];
    }else if(instruction.getA() < SimulatorItems.SEASHIP_BEGIN){
        //dit is een lorry platform
        LorryPlatform lp = _items.getLorryPlatforms()[instruction.getA() - SimulatorItems.LORRY_BEGIN];
        lp.getShipment().arrived = true;

    } else if (instruction.getA() < SimulatorItems.STORAGE_BEGIN) {
        //dit is een seaship platform
        platform = _items.getTrainPlatforms()[instruction.getA() - SimulatorItems.SEASHIP_BEGIN];
    }else if(instruction.getA() < SimulatorItems.TRAIN_BEGIN){
        //dit is een storage platform

    } else {
        //dit is een train platform
        platform = _items.getTrainPlatforms()[instruction.getA() - SimulatorItems.TRAIN_BEGIN];

    }
}

    @Override
    public void forwardResponse(InstructionProto.InstructionResponse resp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}