package at.dcosta.trains.model;

import at.dcosta.trains.configuration.ConfigurationReader;
import at.dcosta.trains.configuration.TrainDepot;
import at.dcosta.trains.error.TrainBuilderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class TrainTest {

    private TrainDepot trainDepot;

    @BeforeEach
    void readDepot() {
        try (InputStream in = TrainTest.class.getClassLoader().getResourceAsStream("trainDepot.yaml")) {
            trainDepot = ConfigurationReader.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void depotIsValid() {
        assertEquals(3, trainDepot.getEngines().size());
        assertEquals(3, trainDepot.getWaggons().size());
    }

    @Test
    void testTrainElementsUnique() {
        Train train = new Train("Orient-Express", trainDepot.getEngines().get(0));
        assertEquals(1, train.getVehicleCount());
        train.add(trainDepot.getEngines().get(1));
        assertEquals(2, train.getVehicleCount());

        // trying to add the first engine a second time -> should throw an Exception
        assertThrows(TrainBuilderException.class, () -> train.add(trainDepot.getEngines().get(0)));
    }

    @Test
    void testTrainCharacteristics() {
        Engine engine0 = trainDepot.getEngines().get(0);
        Engine engine1 = trainDepot.getEngines().get(1);
        Waggon waggon0 = trainDepot.getWaggons().get(0);
        Waggon waggon1 = trainDepot.getWaggons().get(1);

        Train train = new Train("Test Train", engine0);
        train.add(waggon0).add(waggon1).add(engine1);

        int totalWeight = engine0.getEmptyWeight() + engine1.getEmptyWeight() + waggon0.getEmptyWeight() + waggon1.getEmptyWeight();
        assertEquals(totalWeight, train.getEmptyWeight());

        int passengerCapacity = engine0.getPassengerCapacity() + engine1.getPassengerCapacity() + waggon0.getPassengerCapacity() + waggon1.getPassengerCapacity();
        assertEquals(passengerCapacity, train.getPassengerCapacity());

        int freightCapacity = engine0.getFreightCapacity() + engine1.getFreightCapacity() + waggon0.getFreightCapacity() + waggon1.getFreightCapacity();
        assertEquals(freightCapacity, train.getFreightCapacity());

        int overallWeightCapacity = passengerCapacity * 75 + freightCapacity;
        assertEquals(overallWeightCapacity, train.getOverallWeightCapacity());

        int overallWeight = totalWeight + overallWeightCapacity;
        assertEquals(overallWeight, train.getOverallWeigth());

        int length = engine0.getLength() + engine1.getLength() + waggon0.getLength() + waggon1.getLength();
        assertEquals(length, train.getLength());

        int weightToMove = overallWeight - engine0.getEmptyWeight() - engine1.getEmptyWeight();
        assertEquals(weightToMove, train.getTotalWeightToBeMovedByEngines());

        // the existing engines are too weak
        assertFalse(train.canTrainRun());

        // add one more engine
        Engine engine2 = trainDepot.getEngines().get(2);
        train.add(engine2);
        assertEquals(weightToMove + engine2.getFreightCapacity() + 75 * engine2.getPassengerCapacity(), train.getTotalWeightToBeMovedByEngines());

        // now the engines are strong enough
        assertTrue(train.canTrainRun());
    }

    @Test
    void testTrainComposition() {
        Engine engine0 = trainDepot.getEngines().get(0);
        Engine engine1 = trainDepot.getEngines().get(1);

        Waggon waggon0 = trainDepot.getWaggons().get(0);
        Waggon waggon1 = trainDepot.getWaggons().get(1);

        Train train = new Train("Test Train", engine0);
        train.add(waggon0).add(waggon1);

        // trying to remove the only engine -> should throw an Exception
        assertThrows(TrainBuilderException.class, () -> train.remove(engine0));
        // add another engine
        train.add(engine1);
        // now the first engine can get removed
        train.remove(engine0);

        // trying to add the more passengers than the train can take -> should throw an Exception
        assertThrows(TrainBuilderException.class, () -> train.addPassengers(1 + train.getPassengerCapacity()));
        // fill the train with the maximum passengers
        train.addPassengers(train.getPassengerCapacity());
        // now we can not remove a waggon because the remaining train would not have enough capacity to hold all the passengers
        assertThrows(TrainBuilderException.class, () -> train.remove(waggon1));
        // after removing enough passengers, the waggon can get removed
        train.removePassengers(waggon1.getPassengerCapacity());
        train.remove(waggon1);

        // re-prepare the train for the weight tests
        train.removePassengers(train.getPassengerCount());
        train.add(waggon1);

        // trying to add the more freight than the train can take -> should throw an Exception
        assertThrows(TrainBuilderException.class, () -> train.addFreight(1 + train.getFreightCapacity()));
        // fill the train with the maximum freight
        train.addFreight(train.getFreightCapacity());
        // now we can not remove a waggon because the remaining train would not have enough capacity to hold all the freight
        assertThrows(TrainBuilderException.class, () -> train.remove(waggon0));
        // after removing enough freight, the waggon can get removed
        train.removeFreight(waggon0.getFreightCapacity());
        train.remove(waggon0);
    }


    @Test
    void testVecicleOnlyBelongsToOneTrain() {
        Train train1 = new Train("Test-Train", trainDepot.getEngines().get(0));
        Waggon waggon = trainDepot.getWaggons().get(0);
        train1.add(waggon);

        Train train2 = new Train("Test-Train", trainDepot.getEngines().get(1));
        // The waggon already belongs to train1 -> should throw an Exception
        assertThrows(TrainBuilderException.class, () -> train2.add(waggon));

        // after removing the waggon from train1 it gen get added to train 2
        train1.remove(waggon);
        train2.add(waggon);
    }

    @Test
    void testVecicleCanOnlyGetAddedOnce() {
        Engine engine = trainDepot.getEngines().get(0);
        Train train1 = new Train("Test-Train", engine);
        assertThrows(TrainBuilderException.class, () -> train1.add(engine));
    }

    @Test
    void testAddPassengers() {
        Train train = new Train("Test-Train", trainDepot.getEngines().get(0));
        train.add(trainDepot.getWaggons().get(0));
        assertEquals(0, train.getPassengerCount());
        train.addPassengers(5);
        assertEquals(5, train.getPassengerCount());
        // the train can not take that manny passengers
        assertThrows(TrainBuilderException.class, () -> train.addPassengers(50));

        // after adding a waggon, the passengers can get added
        train.add(trainDepot.getWaggons().get(1));
        train.addPassengers(50);
        assertEquals(55, train.getPassengerCount());
    }

    @Test
    void testConductors() {
        Train train = new Train("Test-Train", trainDepot.getEngines().get(0));
        train.add(trainDepot.getWaggons().get(0));
        train.add(trainDepot.getWaggons().get(1));
        assertEquals(0, train.getMinimumConductorsNecessary());
        train.addPassengers(5);
        assertEquals(1, train.getMinimumConductorsNecessary());
        train.addPassengers(50);
        assertEquals(2, train.getMinimumConductorsNecessary());
    }

}