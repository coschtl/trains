package at.dcosta.trains.model;


import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Getter
@Jacksonized
@SuperBuilder
@ToString
public class Waggon extends Vehicle {

    public static WaggonBuilder<?, ?> builder() {
        return new ValidatingWaggonBuilder();
    }

    @NotNull
    private WaggonType type;

    private static class ValidatingWaggonBuilder extends WaggonBuilder<Waggon, ValidatingWaggonBuilder> {
        private ValidatingWaggonBuilder() {
        }

        public Waggon build() {
            Waggon waggon = new Waggon(this);
            waggon.validate();
            return waggon;
        }

        @Override
        protected ValidatingWaggonBuilder self() {
            return this;
        }
    }
}
