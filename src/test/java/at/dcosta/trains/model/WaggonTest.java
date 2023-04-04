package at.dcosta.trains.model;

import at.dcosta.trains.error.TrainBuilderException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WaggonTest {
    private static final int LENGTH = 15;

    @Test
    void validWaggon() {
        Waggon.WaggonBuilder<?, ?> builder = Waggon.builder();
        int emptyWeight = 5000;
        setAllPropertiesUsingEmptyWeight(builder, emptyWeight);
        Waggon waggon = builder.build();
        assertEquals(emptyWeight, waggon.getEmptyWeight());
        assertEquals(LENGTH, waggon.getLength());
    }

    @Test
    void invalidWaggon() {
        Waggon.WaggonBuilder<?, ?> builder = Waggon.builder();
        int emptyWeight = 500;
        setAllPropertiesUsingEmptyWeight(builder, emptyWeight);
        assertThrows(TrainBuilderException.class, () -> builder.build());
    }

    private void setAllPropertiesUsingEmptyWeight(Waggon.WaggonBuilder<?, ?> builder, int emptyWeight) {
        builder.typeName("Coole Diesellok")
                .manufacturer("Duftner")
                .type(WaggonType.DINER)
                .emptyWeight(emptyWeight)
                .freightCapacity(500)
                .length(LENGTH)
                .serialNumber(UUID.randomUUID())
                .passengerCapacity(0)
                .manufactureYear(1930);
    }
}
