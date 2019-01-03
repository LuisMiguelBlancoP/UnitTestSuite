package optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

class TestOptional {

	private final String DEFAULT_TEXT = "default";
	private final String TEST_TEXT = "test text";

	@Test
	void optionalCreationTest() {
		// create empty Optional
		Optional<String> emptyOpt1 = Optional.empty();
		Optional<Car> emptyOpt2 = Optional.ofNullable(null);

		// of function throws an exception when the argument is null
		assertThrows(NullPointerException.class, () -> {
			Optional.of(null);
		});

		// create Optional with value
		OptionalInt opt1 = OptionalInt.of(18);
		Optional<String> opt2 = Optional.ofNullable(TEST_TEXT);

		assertAll(() -> assertFalse(emptyOpt1.isPresent()), () -> assertEquals("Optional.empty", emptyOpt2.toString()),
				() -> assertEquals("OptionalInt[18]", opt1.toString()),
				() -> assertEquals("Optional[test text]", opt2.toString()));

	}

	@Test
	void ifPresentOptionalTest() {
		// create empty Optional
		Optional<String> emptyOpt1 = Optional.empty();

		// create Optional with value
		OptionalInt opt1 = OptionalInt.of(18);
		Optional<String> opt2 = Optional.of(TEST_TEXT);

		opt1.ifPresent(value -> assertEquals(18, value));

		// ifPresent function throws an exception when the argument is null
		Consumer<String> nullConsumer = null;
		assertThrows(NullPointerException.class, () -> {
			opt2.ifPresent(nullConsumer);
		});

		// if the consumer is executed when the Optional is empty, then fail
		Consumer<String> consumer = value -> fail();
		emptyOpt1.ifPresent(consumer);
	}

	@Test
	void defaultOptionalValueTest() {
		// create empty Optional
		Optional<String> emptyOpt1 = Optional.empty();

		// create Optional with value
		OptionalInt opt1 = OptionalInt.of(18);
		Optional<String> opt2 = Optional.of(TEST_TEXT);

		int intValue = opt1.orElse(123);
		String stringValue = emptyOpt1.orElseGet(() -> DEFAULT_TEXT);
		String nullValue = emptyOpt1.orElse(null);

		assertAll(() -> assertEquals(18, intValue), () -> assertEquals(DEFAULT_TEXT, stringValue),
				() -> assertEquals(null, nullValue));

		// if the supplier is executed with the orElseGet function, then fail
		Supplier<String> supplier = () -> {
			fail();
			return DEFAULT_TEXT;
		};
		opt2.orElseGet(supplier);

		// orElseGet function throws an exception when the supplier is null
		Supplier<String> nullSupplier = null;
		assertThrows(NullPointerException.class, () -> {
			emptyOpt1.orElseGet(nullSupplier);
		});

		// if value is not present orElseThrow function throws an exception
		assertThrows(IllegalArgumentException.class, () -> {
			emptyOpt1.orElseThrow(IllegalArgumentException::new);
		});

		// assertThrows function throws an exception when the supplier is null
		assertThrows(NullPointerException.class, () -> {
			emptyOpt1.orElseThrow(null);
		});
	}

	@Test
	void getOptionalValueTest() {
		// create empty Optional
		Optional<Car> emptyOpt1 = Optional.empty();

		// create Optional with value
		OptionalInt opt1 = OptionalInt.of(18);
		Optional<String> opt2 = Optional.of(TEST_TEXT);

		int intValue = opt1.getAsInt();
		String stringValue = opt2.get();

		assertAll(() -> assertEquals(18, intValue), () -> assertEquals(TEST_TEXT, stringValue));

		// get function throws an exception when the value is not present
		assertThrows(NoSuchElementException.class, () -> {
			emptyOpt1.get();
		});

	}

	@Test
	void filterOptionalValueTest() {
		// create empty Optional
		Optional<Car> emptyOpt1 = Optional.empty();

		// create Optional with value
		Optional<Integer> age = Optional.of(18);
		Optional<String> opt2 = Optional.of(TEST_TEXT);

		Predicate<Integer> predicate = value -> value > 17;
		boolean isAdult = age.filter(predicate).isPresent();

		// nested filters
		boolean matchAndStarWithT = opt2.filter(value -> TEST_TEXT.equals(value)).filter(value -> value.startsWith("t"))
				.isPresent();

		// only evaluate if value is present
		boolean hasMotor = emptyOpt1.filter(value -> value.getMotor().isPresent()).isPresent();

		assertAll(() -> assertTrue(isAdult), () -> assertTrue(matchAndStarWithT), () -> assertFalse(hasMotor));

		// filter function throws an exception when the predicate is null
		Predicate<Car> nullPredicate = null;
		assertThrows(NullPointerException.class, () -> {
			emptyOpt1.filter(nullPredicate);
		});

	}

	@Test
	void mapOptionalValueTest() {
		// create empty Optional
		Optional<Car> emptyOpt1 = Optional.empty();

		// create Optional with value
		Optional<String> opt1 = Optional.of(TEST_TEXT);

		// create a Car given String
		Car car = opt1.map(value -> new Car(new Motor(value))).orElseGet(() -> new Car(new Motor(DEFAULT_TEXT)));
		// get the serial created by the map
		String serial = car.getMotor().filter(value2 -> value2.getSerial().isPresent()).get().getSerial().get();

		// create a String given an Optional Car, but optional is empty
		String notSerial = emptyOpt1
				.map(value -> value.getMotor().filter(value2 -> value2.getSerial().isPresent()).get().getSerial().get())
				.orElse(DEFAULT_TEXT);

		assertAll(() -> assertEquals(TEST_TEXT, serial), () -> assertEquals(DEFAULT_TEXT, notSerial));

		// map function throws an exception when the function is null
		Function<String, Car> nullFunction = null;
		assertThrows(NullPointerException.class, () -> {
			opt1.map(nullFunction);
		});
	}

	@Test
	void flatMapOptionalValueTest() {
		// create empty Optional
		Optional<Car> emptyOpt1 = Optional.empty();

		// create Optional with value
		Optional<String> opt1 = Optional.of(TEST_TEXT);

		// create an Optional Car given String
		Optional<Car> optCar = opt1.map(value -> Optional.of(new Car(new Motor(value))))
				.orElseGet(() -> Optional.of(new Car(new Motor(DEFAULT_TEXT))));
		// get the serial of the Optional car created by the map
		String serial = optCar.flatMap(value -> value.getMotor().flatMap(value2 -> value2.getSerial())).orElse(DEFAULT_TEXT);

		// create a String given an Optional Car, but optional is empty
		String notSerial = emptyOpt1.flatMap(value -> value.getMotor().flatMap(value2 -> value2.getSerial()))
				.orElse(DEFAULT_TEXT);

		assertAll(() -> assertEquals(TEST_TEXT, serial), () -> assertEquals(DEFAULT_TEXT, notSerial));

		// flatMap function throws an exception when the function is null
		Function<String, Optional<Car>> nullFunction = null;
		assertThrows(NullPointerException.class, () -> {
			opt1.flatMap(nullFunction);
		});
	}
}
