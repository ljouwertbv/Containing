package nhl.containing.controller.simulation;

import java.util.*;
import nhl.containing.controller.*;

/**
 * Encapsulates information about shipments of a simulation instance.
 *
 * <p> The {@code formRecordSet} method groups the raw record data from the XML
 * file into {@code Shipment}s. A {@code Shipment} represents an incoming or
 * outgoing shipment by a carrier which carries a certain amount of containers
 * and is is identified by a key based on the arrival or departure data. The
 * shipment data can be accessed by the {@code getShipments} method. Shipments
 * by a specific carrier type can be access with {@code getX} where {@code X} is
 * either {@code SeaShips}, {@code InlandShips}, {@code Trucks} or
 * {@code Trains}.
 *
 * @author henkmollema
 */
public class SimulationContext
{
    private Map<String, Shipment> shipments = new HashMap<>();

    public Collection<Shipment> getShipments()
    {
        return Collections.unmodifiableCollection(shipments.values());
    }

    /**
     * Creates a simulation context from the specified record set.
     *
     * @param recordSet The record set.
     *
     * @return An instance of SimulationContext.
     *
     * @throws Exception when the carrier type is unknown.
     */
    public static SimulationContext fromRecordSet(RecordSet recordSet) throws Exception
    {
        SimulationContext context = new SimulationContext();

        for (Record r : recordSet.records)
        {
            Arrival arrival = r.arrival;
            String arrivalKey = arrival.company + arrival.date.day
                                + arrival.date.month + arrival.date.year
                                + arrival.time.from + arrival.time.until;

            // Find an existing shipment.
            Shipment arrivalShipment = findShipmentByKey(context.shipments, arrivalKey);
            if (arrivalShipment == null)
            {
                // No existing shipment - create a new shipment.
                arrivalShipment = new Shipment(arrivalKey, true);
                Carrier carrier = parseCarrier(arrival.transportType);
                carrier.company = arrival.company;
                arrivalShipment.carrier = carrier;
                context.shipments.put(arrivalKey, arrivalShipment);
            }

            Departure departure = r.departure;
            String departureKey = departure.company + departure.date.day
                                  + departure.date.month + departure.date.year
                                  + departure.time.from + departure.time.until;

            // Find an existing shipment.
            Shipment departureShipment = findShipmentByKey(context.shipments, departureKey);
            if (departureShipment == null)
            {
                // No existing shipment - create a new shipment.
                departureShipment = new Shipment(departureKey, false);
                Carrier carrier = parseCarrier(departure.transportType);
                carrier.company = departure.company;
                departureShipment.carrier = carrier;
                context.shipments.put(departureKey, departureShipment);
            }

            // Add container data.
            ShippingContainer c = new ShippingContainer();
            c.position = arrival.position;
            c.containerNumber = r.containerNumber;
            c.content = r.content;
            c.contentDanger = r.contentDanger;
            c.contentType = r.contentType;
            c.height = r.height;
            c.iso = r.iso;
            c.length = r.length;
            c.ownerName = r.ownerName;
            c.weightLoaded = r.weightLoaded;
            c.weigthEmpty = r.weigthEmpty;
            c.width = r.width;

            // Add to arrival and departure shipment.
            arrivalShipment.carrier.containers.add(c);
            departureShipment.carrier.containers.add(c);
        }

        return context;
    }

    private static Shipment findShipmentByKey(Map<String, Shipment> shipments, String key)
    {
        if (shipments.containsKey(key))
        {
            return shipments.get(key);
        }

        return null;
    }

    private static Carrier parseCarrier(String transportType) throws Exception
    {
        switch (transportType)
        {
            case "zeeschip":
                return new SeaShip();
            case "binnenschip":
                return new InlandShip();
            case "vrachtauto":
                return new Truck();
            case "trein":
                return new Train();
        }

        throw new Exception("Invalid carrier: " + transportType);
    }

    private static boolean shouldSkip(Boolean incoming, boolean candidateIncoming)
    {
        if (incoming != null)
        {
            // Filter for incoming is specified.
            if (incoming && !candidateIncoming)
            {
                // Filtered by incoming, but candidate is not an incoming shipment.
                return true;
            }
            if (!incoming && candidateIncoming)
            {
                // Filtered by outgoing, but candidate is an incomping shipment
                return true;
            }
        }

        // No filter for incoming specified or filter matches candidate.
        return false;
    }

    public Collection<SeaShip> getSeaShips(Boolean incoming)
    {
        return filter(SeaShip.class, incoming);
    }

    public Collection<InlandShip> getInlandShips(Boolean incoming)
    {
        return filter(InlandShip.class, incoming);
    }

    public Collection<Truck> getTrucks(Boolean incoming)
    {
        return filter(Truck.class, incoming);
    }

    public Collection<Train> getTrains(Boolean incoming)
    {
        return filter(Train.class, incoming);
    }

    private <T extends Carrier> Collection<T> filter(Class<T> t, Boolean incoming)
    {
        List<T> carriers = new ArrayList<>();
        for (Shipment candidate : shipments.values())
        {
            if (shouldSkip(incoming, candidate.incoming))
            {
                continue;
            }

            if (t.isInstance(candidate.carrier))
            {
                carriers.add((T) candidate.carrier);
            }
        }
        return Collections.unmodifiableCollection(carriers);
    }
}