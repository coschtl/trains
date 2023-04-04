package at.dcosta.trains.model;

import at.dcosta.trains.error.TrainBuilderException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Train {

    private final List<Vehicle> vehicles;
    private final String name;
    private int passengers;
    private int freightWeight;

    public Train(String name, Engine engine) {
        if (name == null) {
            throw new TrainBuilderException("Train name must not be null!");
        }
        this.name = name;
        vehicles = new ArrayList<>();
        add(engine);
    }

    public Train add(Vehicle vehicle) {
        if (vehicle == null) {
            throw new TrainBuilderException("Vehicle to add must not be null!");
        }
        if (vehicles.contains(vehicle)) {
            throw new TrainBuilderException("The " + vehicle.getClass().getSimpleName() + " with SerialNumber " + vehicle.getSerialNumber() + " is already part of this train!");
        }
        if (vehicle.getMemberOfTrain() != null) {
            throw new TrainBuilderException("The " + vehicle.getClass().getSimpleName() + " with SerialNumber " + vehicle.getSerialNumber() + " already belongs to the train '" + vehicle.getMemberOfTrain().getName() + "'!");
        }
        vehicle.setMemberOfTrain(this);
        vehicles.add(vehicle);
        return this;
    }

    public Train remove(Vehicle vehicle) {
        if (!vehicles.contains(vehicle)) {
            throw new TrainBuilderException("The " + vehicle.getClass().getSimpleName() + " with SerialNumber " + vehicle.getSerialNumber() + " is not part of this train!");
        }
        if (vehicle instanceof Engine && enginesAsStream().count() < 2) {
            throw new TrainBuilderException("The engine with SerialNumber " + vehicle.getSerialNumber() + " must not get removed because it is the last engine and every train needs an engine!");
        }
        checkPassengersPossible(getPassengerCount() + vehicle.getPassengerCapacity());
        checkFreightPossible(getFreightWeight() + vehicle.getFreightCapacity());
        vehicles.remove(vehicle);
        vehicle.setMemberOfTrain(null);
        return this;
    }

    /**
     * Es kann das Leergewicht des gesamten Zuges abgefragt werden.
     */
    public int getEmptyWeight() {
        return vehicles.stream().map(vehicle -> vehicle.getEmptyWeight()).reduce(Integer::sum).get();
    }


    /**
     * Es kann die maximale Anzahl an Passagieren pro Zug abgefragt werden
     */
    public int getPassengerCapacity() {
        return vehicles.stream().map(vehicle -> vehicle.getPassengerCapacity()).reduce(Integer::sum).get();
    }

    /**
     * Es kann das maximale Zuladungsgewicht für Güter pro Zug abgefragt werden
     */
    public int getFreightCapacity() {
        return vehicles.stream().map(vehicle -> vehicle.getFreightCapacity()).reduce(Integer::sum).get();
    }

    /**
     * Es kann die maximale Zuladung eines Zuges abgefragt werden (= maximale Anzahl
     * der Passagiere im Zug x 75kg + maximales Zuladungsgewicht für Güter)
     */
    public int getOverallWeightCapacity() {
        return getPassengerCapacity() * 75 + getFreightCapacity();
    }

    /**
     * Es kann das maximale Gesamtgewicht des Zuges abgefragt werden
     */
    public int getOverallWeigth() {
        return getEmptyWeight() + getOverallWeightCapacity();
    }

    /**
     * Es kann die Länge des Zuges abgefragt werden
     */
    public int getLength() {
        return vehicles.stream().map(vehicle -> vehicle.getLength()).reduce(Integer::sum).get();
    }


    /**
     * Hinzufügen von Passagieren
     */
    public Train addPassengers(int passengers) {
        if (passengers < 0) {
            throw new TrainBuilderException("passengers argument must be > 0!");
        }
        checkPassengersPossible(this.passengers + passengers);
        this.passengers += passengers;
        return this;
    }

    public Train removePassengers(int passengers) {
        if (passengers < 0) {
            throw new TrainBuilderException("passengers argument must be > 0!");
        }
        if (passengers > this.passengers) {
            throw new TrainBuilderException("This train does not have that much passengers!");
        }
        this.passengers -= passengers;
        return this;
    }

    private void checkPassengersPossible(int totalPassengers) {
        if (getPassengerCapacity() < totalPassengers) {
            throw new TrainBuilderException("Too manny passengers!");
        }
    }


    /**
     * Hinzufügen von Fracht
     */
    public Train addFreight(int freightWeight) {
        if (freightWeight < 0) {
            throw new TrainBuilderException("freightWeight argument must be > 0!");
        }
        checkFreightPossible(this.freightWeight + freightWeight);
        this.freightWeight += freightWeight;
        return this;
    }

    public Train removeFreight(int freightWeight) {
        if (freightWeight < 0) {
            throw new TrainBuilderException("freightWeight argument must be > 0!");
        }
        if (freightWeight > this.freightWeight) {
            throw new TrainBuilderException("This train does not have that much freight!");
        }
        this.freightWeight -= freightWeight;
        return this;
    }


    private void checkFreightPossible(int totalFreightWeight) {
        if (getFreightCapacity() < totalFreightWeight) {
            throw new TrainBuilderException("Too much freightWeight!");
        }
    }

    /**
     * Es kann die Zahl der maximal benötigten Schaffner pro Zug abgefragt werden
     */
    public int getMinimumConductorsNecessary() {
        if (passengers == 0) {
            return 0;
        }
        return 1 + (passengers / 50);
    }

    /**
     * Es soll geprüft werden können, ob der Zug fahrfähig ist. Das heißt, ob die im Zug
     * vorhandenen Lokomotiven in der Lage sind, den Zug mit maximaler Zuladung
     * (Passagiere und Güter) zu ziehen
     */
    public boolean canTrainRun() {
        int totalTraction = enginesAsStream().map(engine -> engine.getTraction()).reduce(Integer::sum).get();
        return totalTraction >= getTotalWeightToBeMovedByEngines();
    }

    int getTotalWeightToBeMovedByEngines() {
        return getOverallWeigth() - enginesAsStream().map(engine -> engine.getEmptyWeight()).reduce(Integer::sum).get();
    }

    private Stream<Engine> enginesAsStream() {
        return vehicles.stream().filter(vehicle -> vehicle instanceof Engine).map(vehicle -> ((Engine) vehicle));
    }

    public int getVehicleCount() {
        return vehicles.size();
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public int getPassengerCount() {
        return passengers;
    }

    public int getFreightWeight() {
        return freightWeight;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Train train = (Train) o;
        return Objects.equals(name, train.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
