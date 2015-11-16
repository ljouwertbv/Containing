package controller;

import java.awt.Point;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.logging.*;

@SuppressWarnings("all")
public class Main
{
    static {
        try {
            File file = new File(Main.class.getResource("/lib/").toURI().toString(), "JNITest.dll");
            File resFile = new File(System.getProperty("java.io.tmpdir"), "JNITest.dll");
            if (!resFile.exists()) {
                resFile.createNewFile();
            }
            Main.copyFileToTemp(file.getPath().substring(5), resFile.getAbsolutePath());
            System.load(resFile.getAbsolutePath());
            //System.loadLibrary("JNITest");
        }
        catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        run();
        
//        JNITest.helloFromC();
//        int[] iA = { 5, 6, 8 };
//        System.out.println("average: " + JNITest.avgFromC(iA));
//        System.out.println("average int: " + JNITest.intFromC(iA));
//        Integer i = JNITest.integerFromC(5);
//        System.out.println(i);
//        Point point = JNITest.pointInC(5, 5);
//        System.out.println("x: " + point.x + " y: " + point.y);
//        JNITest test = new JNITest();
//        test.changeNumberInC();
//        System.out.println(test.getNumber());
    }
    
    /**
     * Run Forest! Run!
     */
    private static void run()
    {
        SimulatorController controller = new SimulatorController();
        try {
            controller.run("xml1.xml");
            controller.run("xml2.xml");
            controller.run("xml3.xml");
            controller.run("xml4.xml");
            controller.run("xml5.xml");
            //controller.run("xml6.xml");
            //controller.run("xml7.xml");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void copyFileToTemp(String source, String dest) throws IOException
    {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
        catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
}
