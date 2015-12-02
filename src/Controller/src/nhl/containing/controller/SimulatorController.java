package nhl.containing.controller;

import java.net.*;
import java.nio.file.*;
import java.util.HashSet;
import nhl.containing.controller.simulation.SimulationContext;

/**
 * Controller for the system.
 *
 * @author henkmollema
 */
public class SimulatorController
{
    private final Database _db;
    private SimulationContext _context;

    public SimulatorController()
    {
        _db = new Database();
    }

    public SimulationContext getSimulationContext()
    {
        return _context;
    }

    /**
     * Starts running the simulator using the data in the specified XML file
     * name.
     *
     * @param xmlFileName The file name of the XML file. Format: [name].xml.
     *
     * @throws java.lang.Exception when serialization fails.
     */
    public void run(String xmlFileName) throws Exception
    {
        RecordSet recordSet = parseXml(xmlFileName);
        p("Analyzing XML data...");
        long start = System.currentTimeMillis();
        _context = SimulationContext.fromRecordSet(recordSet);
        long elapsed = System.currentTimeMillis() - start;
        p("Analyzed XML data. Elapsed: " + elapsed + "ms");

        writeAnalyzeResults();

        Simulator sim = new Simulator(this);
        if (sim.start())
        {
            if (sim.init(null))
            {
                if (sim.play())
                {
                    for (Record record : recordSet.records)
                    {
                        sim.processRecord(record);
                    }
                }
            }
        }
    }

    private void writeAnalyzeResults()
    {
        p("| Type | Inkomend | Uitgaand | Totaal |");
        p("| --- | --- | --- | --- |");
        p("| Zeeschip | " + _context.getSeaShips(true).size() + " | " + _context.getSeaShips(false).size() + " | " + _context.getSeaShips(null).size() + " |");
        p("| Binnenschip | " + _context.getInlandShips(true).size() + " | " + _context.getInlandShips(false).size() + " | " + _context.getInlandShips(null).size() + " |");
        p("| Vrachtwagen | " + _context.getTrucks(true).size() + " | " + _context.getTrucks(false).size() + " | " + _context.getTrucks(null).size() + " |");
        p("| Trein | " + _context.getTrains(true).size() + " | " + _context.getTrains(false).size() + " | " + _context.getTrains(null).size() + " |");
    }

    private static void p(String s)
    {
        System.out.println(s);
    }

    private RecordSet parseXml(String xmlFileName) throws Exception
    {
        String xmlString = readXml(xmlFileName);
        RecordSet recordSet = XmlParser.parse(xmlString);

        if (recordSet == null)
        {
            throw new Exception("Something went wrong when deserializing the XML file. ");
        }

        if (hasDuplicateIds(recordSet))
        {
            throw new Exception("Record set contains duplicate ID's.");
        }

        System.out.println("Parsed " + recordSet.records.size() + " records");
        return recordSet;
    }

    private boolean hasDuplicateIds(RecordSet recordSet)
    {
        HashSet<String> hashSet = new HashSet<>();
        for (Record record : recordSet.records)
        {
            if (hashSet.contains(record.id))
            {
                return true;
            }
            hashSet.add(record.id);
        }
        return false;
    }

    private String readXml(String xmlFileName)
    {
        try
        {
            URI url = Main.class.getResource("XML/" + xmlFileName).toURI();
            return new String(Files.readAllBytes(Paths.get(url)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Marks the specified record object as processed
     *
     * @param record The record object to mark as processed.
     */
    public void markAsProcessed(Record record)
    {
        // todo: mark as processed in db.
        _db.saveRecord(record);
    }
}
