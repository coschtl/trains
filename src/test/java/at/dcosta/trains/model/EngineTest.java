package at.dcosta.trains.model;

import at.dcosta.trains.error.TrainBuilderException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EngineTest {

    private static final int LENGTH = 15;

    @Test
    void validEngine() {
        Engine.EngineBuilder<?, ?> builder = Engine.builder();
        int emptyWeight = 5000;
        setAllPropertiesUsingEmptyWeight(builder, emptyWeight);
        Engine engine = builder.build();
        assertEquals(emptyWeight, engine.getEmptyWeight());
        assertEquals(LENGTH, engine.getLength());
    }

    @Test
    void invalidEngine() {
        Engine.EngineBuilder<?, ?> builder = Engine.builder();
        int emptyWeight = 500;
        setAllPropertiesUsingEmptyWeight(builder, emptyWeight);
        assertThrows(TrainBuilderException.class, () -> builder.build());
    }

    private void setAllPropertiesUsingEmptyWeight(Engine.EngineBuilder<?, ?> builder, int emptyWeight) {
        builder.typeName("Coole Diesellok")
                .manufacturer("Duftner")
                .traction(50000)
                .type(EngineType.DIESEL)
                .emptyWeight(emptyWeight)
                .freightCapacity(500)
                .length(LENGTH)
                .serialNumber(UUID.randomUUID())
                .passengerCapacity(0)
                .manufactureYear(1930);
    }
}
