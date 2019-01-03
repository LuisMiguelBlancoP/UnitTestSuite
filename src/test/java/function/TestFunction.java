package function;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;
import java.util.function.UnaryOperator;

import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Comparators;
import com.google.common.util.concurrent.Uninterruptibles;

class TestFunction {

	private final String LONG_TEXT = "Really long sentence";
	private final String PRE_LONG_TEXT = "The text for the supplier test is: ";

	@Test
	void functionTest() {

		Function<String, Integer> stringSize = String::length;
		Function<Integer, String> isAdult = value -> value >= 18 ? "Adult Person" : "Joung";
		IntToDoubleFunction squareRoot = Math::sqrt;

		// isAdult is executed first
		Function<Integer, Integer> sizeOfAgeCategory = stringSize.compose(isAdult);
		// stringSize is executed first
		Function<String, String> wordSizeIsAdult = stringSize.andThen(isAdult);

		assertEquals(new Integer(12), sizeOfAgeCategory.apply(19));
		assertEquals("Adult Person", wordSizeIsAdult.apply(LONG_TEXT));
		assertEquals(5, squareRoot.applyAsDouble(25));

		// compose function throws an exception when the given function is null
		assertThrows(NullPointerException.class, () -> {
			stringSize.compose(null);
		});

		// andThen function throws an exception when the given function is null
		assertThrows(NullPointerException.class, () -> {
			stringSize.andThen(null);
		});
	}

	@Test
	void biFunctionTest() {

		BiFunction<String, String, String> joinStrings = (a, b) -> a.concat(b);
		Function<String, String> showResult = value -> "the result is: " + value;
		ToDoubleBiFunction<Integer, Integer> powNumbers = Math::pow;

		BiFunction<String, String, String> showStringsJoinedAsResult = joinStrings.andThen(showResult);

		assertEquals("the result is: Lorem Ipsum", showStringsJoinedAsResult.apply("Lorem ", "Ipsum"));
		assertEquals(16, powNumbers.applyAsDouble(2, 4));

		// andThen function throws an exception when the given function is null
		assertThrows(NullPointerException.class, () -> {
			joinStrings.andThen(null);
		});
	}

	@Test
	void supplierTest() {
		Supplier<String> supplier = () -> {
			Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
			return LONG_TEXT;
		};

		String finalString = getLongString(supplier);
		assertEquals(PRE_LONG_TEXT + LONG_TEXT, finalString);
	}

	private String getLongString(Supplier<String> value) {
		return PRE_LONG_TEXT.concat(value.get());
	}

	@Test
	void consumerTest() {

		// consumer applied to many values
		List<Country> countries = Arrays.asList(new Country("Colombia", true), new Country("Canada", true),
				new Country("Bolivia", false));
		Consumer<Country> seaForAll = value -> value.setSea(true);
		countries.forEach(seaForAll);

		boolean countryWithoutSea = countries.stream().anyMatch(c -> !c.haveSea().orElse(false));
		assertFalse(countryWithoutSea);

		// consumer applied to one value
		Country bolivia = new Country("Bolivia", false);
		seaForAll.accept(bolivia);

		assertTrue(bolivia.haveSea().orElse(false));

		// chain many consumers for the same value
		Country suiza = new Country("Suiza", false);
		Consumer<Country> extraConsumer = value -> value.setName(value.getName().orElse("default") + " get sea");
		Consumer<Country> consumerChained = seaForAll.andThen(extraConsumer);
		consumerChained.accept(suiza);

		assertAll(() -> assertTrue(suiza.haveSea().orElse(false)),
				() -> assertEquals("Suiza get sea", suiza.getName().orElse("default")));

		// andThen function throws an exception when the given function is null
		assertThrows(NullPointerException.class, () -> {
			seaForAll.andThen(null);
		});

	}

	@Test
	void predicateTest() {
		Predicate<Integer> isAdult = value -> value >= 18;
		Predicate<Integer> isSoOld = value -> value >= 65;
		Predicate<Country> isColombian = value -> "Colombia".equals(value.getName().orElse("default"));
		Optional<Integer> optInteger = Optional.of(35);

		// predicate as parameter in filter
		assertTrue(optInteger.filter(isAdult).isPresent());

		// if the argument match with the predicate, then correct
		assertTrue(isAdult.test(35));
		// negate the predicate logic
		assertTrue(isColombian.negate().test(new Country("Canada", true)));
		// logic OR
		assertTrue(isAdult.or(isSoOld).test(30));
		// logic AND
		assertFalse(isAdult.and(isSoOld).test(30));
		// Different predicates
		assertFalse(isAdult.equals(isSoOld));

		// or function throws an exception when the given predicate is null
		assertThrows(NullPointerException.class, () -> {
			isAdult.or(null);
		});

		// and function throws an exception when the given predicate is null
		assertThrows(NullPointerException.class, () -> {
			isAdult.and(null);
		});

	}

	@Test
	void operatorTest() {
		UnaryOperator<String> addPrefix = value -> "Prefix: " + value;
		BinaryOperator<Long> product = Math::multiplyExact;

		String stringWithPrefix = addPrefix.apply(LONG_TEXT);
		Long productResult = product.apply(2L, 2L);

		assertEquals("Prefix: " + LONG_TEXT, stringWithPrefix);
		assertEquals(4, productResult.longValue());

		// find the max value
		BinaryOperator<Integer> bi = BinaryOperator.maxBy(Comparator.naturalOrder());
		assertEquals(8, bi.apply(2, 8).intValue());

		// maxBy function throws an exception when the given comparator is null
		assertThrows(NullPointerException.class, () -> {
			BinaryOperator.maxBy(null);
		});

		// minBy function throws an exception when the given comparator is null
		assertThrows(NullPointerException.class, () -> {
			BinaryOperator.minBy(null);
		});

	}
}
