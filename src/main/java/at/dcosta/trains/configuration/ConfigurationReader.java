package at.dcosta.trains.configuration;

import at.dcosta.trains.error.TrainBuilderException;
import at.dcosta.trains.model.Vehicle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ConfigurationReader {
    public static TrainDepot read(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        TrainDepot trainDepot = objectMapper.readValue(inputStream, TrainDepot.class);

        // jackson does not use the validating builders but the default builders
        // a lombok bug?
        trainDepot.getWaggons().stream().forEach(waggon -> waggon.validate());
        trainDepot.getEngines().stream().forEach(engine -> engine.validate());

        // assure that serailNumbers are unique
        Set<UUID> serials = new HashSet<>();
        assureUniqueSerials(trainDepot.getWaggons(), serials);
        assureUniqueSerials(trainDepot.getEngines(), serials);
        return trainDepot;
    }

    private static void assureUniqueSerials(List<? extends Vehicle> vehicles, Set<UUID> serials) {
        for (Vehicle vehicle : vehicles) {
            if (!serials.add(vehicle.getSerialNumber())) {
                throw new TrainBuilderException("the configuration contains more than one entry with SerialNumber=" + vehicle.getSerialNumber());
            }
        }
    }
}
