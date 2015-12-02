/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.networking;

import nhl.containing.networking.messaging.MessageWriter;
import java.io.*;
import java.net.Socket;
import nhl.containing.networking.messaging.MessageReader;
import nhl.containing.networking.protobuf.ClientIdProto.ClientIdentity;
import nhl.containing.networking.protobuf.InstructionProto.Instruction;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionType;

/**
 * Providers interaction with the client.
 *
 * @author Jens
 */
public class SimulatorClient implements Runnable {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 1337;
    private boolean isConnected;
    private boolean shouldRun;
    private Socket _socket = null;
    private CommunicationProtocol controllerCom;

    public SimulatorClient() {
        controllerCom = new CommunicationProtocol();
    }

    /**
     * Gets the communication protocol of the server.
     *
     * @return The communication protocol.
     */
    public CommunicationProtocol controllerCom() {
        return controllerCom;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void stop() {
        shouldRun = false;
    }

    /**
     * Opens a serversocket and waits for a client to connect. This method
     * should be called on it's own thread as it contains an indefinite loop.
     * Returns false if setup/connection failed. Returns true if connection was
     * successfull and closed peacefuly
     */
    public boolean start() {
        p("start()");

        if (!isConnected) {

            try {
                // Halt the thread until a connection has been accepted
                _socket = new Socket(HOST, PORT);

                p("Connected to server!");

                isConnected = true;
                return true;

            } catch (Exception ex) {
                p("Connection refused..");
                //ex.printStackTrace();
                return false;
            }
        }

        return false;
    }

    private boolean sendSimulatorMetadata() {
        p("sendSimulatorMetadata()");

        try {

            BufferedInputStream input = new BufferedInputStream(_socket.getInputStream());
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            OutputStream output = _socket.getOutputStream();
            
            //Tell the Controller we are the simulator
            ClientIdentity.Builder idBuilder = ClientIdentity.newBuilder();
            idBuilder.setClientType(ClientIdentity.ClientType.SIMULATOR)
                     .setVersion(CommunicationProtocol.PROTOCOL_VERSION);
            
            MessageWriter.writeMessage(output, idBuilder.build().toByteArray());
            //Wait for an okay response..
            byte[] ba = MessageReader.readByteArray(input, dataStream);
            Instruction i = Instruction.parseFrom(ba);
            if(i.getInstructionType() != InstructionType.CLIENT_CONNECTION_OKAY) throw new IOException();
            


//            PlatformProto.Platform.Builder platformBuilder = PlatformProto.Platform.newBuilder();
//            platformBuilder
//                    .setId(CommunicationProtocol.newUUID())
//                    .setType(PlatformProto.Platform.PlatformType.SeaShip)
//                    .addCranes(PlatformProto.Platform.Crane.newBuilder()
//                    .setId(CommunicationProtocol.newUUID())
//                    .setType(PlatformProto.Platform.Crane.CraneType.Rails))
//                    .addCranes(PlatformProto.Platform.Crane.newBuilder()
//                    .setId(CommunicationProtocol.newUUID())
//                    .setType(PlatformProto.Platform.Crane.CraneType.Rails));
//
//            PlatformProto.Platform platform = platformBuilder.build();
            
            SimulationItem item = SimulationItem.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setType(SimulationItem.SimulationItemType.PLATFORM)
                    .build();



            byte[] message = item.toByteArray();
            p("Sending " + message.length + " bytes...");

            MessageWriter.writeMessage(output, message);

            p("Message sent to controller, start reading input..");

            String result = new String(MessageReader.readByteArray(input, dataStream), "UTF-8");

            if (result == null || result.equals("")) {
                System.err.println("No result of sendSimulatorMetadata().");
                return false;
            }

            if (result.equalsIgnoreCase("ok")) {
                p("result is " + result);
                return true;
            }

            p("result is " + result);
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean instructionLoop() {
        p("read()");
        try {
            BufferedInputStream input = new BufferedInputStream(_socket.getInputStream());
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            OutputStream output = _socket.getOutputStream();

            //Send empty message to start conversation..
            MessageWriter.writeMessage(output, new byte[]{0});

            while (shouldRun) {
                // Re-use streams for more efficiency.
                byte[] data = MessageReader.readByteArray(input, dataStream);
                byte[] response = controllerCom.processInput(data);

                MessageWriter.writeMessage(output, response);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void run() {
        shouldRun = true;

        while (shouldRun)//While shouldRun, when connection is lost, start listening for a new one
        {
            if (start()) {
                if (sendSimulatorMetadata()) {
                    if (instructionLoop()) {
                        p("Closed peacefully");
                    } else {
                        p("Lost connection during instructionloop");
                    }
                } else {
                    p("Error while initialising connection..");
                }
            } else {
                p("Closed forcefully");
            }

            try //Clean 
            {
                if (_socket != null) {
                    _socket.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            isConnected = false;
        }
    }

    private static void p(String s) {
        System.out.println("Simulator: " + s);
    }
}
