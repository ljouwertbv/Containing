/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Jens
 */
public class Client implements Runnable{
    
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 1337;
    
    public static final int END_OF_TRANSMISSION = 4;
    
    private PrintWriter out = null;
    private BufferedReader in = null;
    private Scanner reader;
    
    CommunicationProtocolClient comProtocol;
    
    boolean isConnected;
    
    Socket socket;
    
    public Client()
    {
        comProtocol = new CommunicationProtocolClient();
    }
    
    /* Try's to connect   
     * 
     */
    public boolean Connect()
    {
        try
        {
            socket = new Socket(HOST, PORT);
            isConnected = true;
            
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            
            //out.println("Client says hello!");
            
            int lastByte;
            boolean shouldBreak = false; 
            while(!shouldBreak)
            {
                while((lastByte = in.read()) > 0)
                    {
                        
                        if(lastByte == END_OF_TRANSMISSION)
                        {
                            byte[] response = comProtocol.processInput(buffer.toByteArray());
                            buffer.reset();
                            
                            //Send response
                            out.write(response);
                            out.write(END_OF_TRANSMISSION);
                            out.flush();
                        }
                        else
                        {
                            //Add current input to buffer
                            buffer.write(lastByte);
                        } 
                    }
            }
        }
        catch(Exception ex)
        {
            System.out.println("Can't connect to controller:");
            System.out.println(ex.toString() +"||" +ex.getMessage());
            
            return false;
        }
        
        
        return true;
    }

    @Override
    public void run() {
        if(Connect())
        {
            System.out.println("Closed peacefully");
        }
        else
        {
            System.out.println("Closed forcefully");
        }
    }
}