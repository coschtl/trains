package at.dcosta.trains.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Jacksonized
@SuperBuilder
@ToString
public class Engine extends Vehicle {

    @Min(value = 5000, message = "An engine must have a traction of at least 5000!")
    private int traction;

    @NotNull
    private EngineType type;

    public static EngineBuilder<?, ?> builder() {
        return new ValidatingEngineBuilder();
    }

    private static class ValidatingEngineBuilder extends EngineBuilder<Engine, ValidatingEngineBuilder> {
        private ValidatingEngineBuilder() {
        }

        public Engine build() {
            Engine engine = new Engine(this);
            engine.validate();
            return engine;
        }

        @Override
        protected ValidatingEngineBuilder self() {
            return this;
        }
    }
}
