package at.dcosta.trains.configuration;

import at.dcosta.trains.model.Engine;
import at.dcosta.trains.model.Waggon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainDepot {

    private final List<Engine> engines;
    private final List<Waggon> waggons;

    public TrainDepot() {
        engines = new ArrayList<>();
        waggons = new ArrayList<>();
    }

    public List<Engine> getEngines() {
        return Collections.unmodifiableList(engines);
    }

    public List<Waggon> getWaggons() {
        return Collections.unmodifiableList(waggons);
    }
}
