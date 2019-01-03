package optional;

import java.util.Optional;

public class Car {

	public Car(Motor motor) {
		this.motor = Optional.ofNullable(motor);
	}

	private Optional<Motor> motor;

	public Optional<Motor> getMotor() {
		return motor;
	}
}
