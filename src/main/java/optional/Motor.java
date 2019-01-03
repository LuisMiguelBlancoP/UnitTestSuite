package optional;

import java.util.Optional;

public class Motor {
	public Motor(String serial) {
		this.serial = Optional.ofNullable(serial);
	}

	private Optional<String> serial;

	public Optional<String> getSerial() {
		return serial;
	}

}
