package at.dcosta.trains.model;

import at.dcosta.trains.error.TrainBuilderException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@SuperBuilder
public abstract class Vehicle {

    private static final Validator VALIDATOR;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }

    public abstract static class VehicleBuilder<C extends Vehicle, B extends VehicleBuilder<C, B>> {
        private B memberOfTrain(Train memberOfTrain) {
            return this.self();
        }
    }

    @Setter
    private Train memberOfTrain;

    @Min(value = 1000, message = "Train vehicles with less than 1000kg can not get constructed!")
    private final int emptyWeight;

    @Min(value = 10, message = "Train vehicles with less than 10m length not get constructed!")
    private final int length;

    @Min(value = 0, message = "passenger capacity must be greate than or equal 0!")
    private final int passengerCapacity;

    @Min(value = 0, message = "freight capacity must be greate than or equal 0!")
    private final int freightCapacity;

    @NotBlank
    private final String typeName;

    @NotBlank
    private final String manufacturer;

    @NotNull
    private final UUID serialNumber;

    // use a Date for easy validation
    @Min(1800)
    @Max(2023)
    private final int manufactureYear;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle that = (Vehicle) o;
        return serialNumber.equals(that.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }

    public void validate() {
        Set<ConstraintViolation<Vehicle>> violations = VALIDATOR.validate(this);
        if (!violations.isEmpty()) {
            String msg = violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).collect(Collectors.joining("\n"));
            throw new TrainBuilderException("The " + getClass().getSimpleName() + " is not valid: \n" + msg);
        }
    }
}
