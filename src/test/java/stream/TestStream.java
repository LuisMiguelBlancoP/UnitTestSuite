package stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Comparators;

class TestStream {

	@Test
	void streamCreationTest() {
		// create an empty stream
		Stream<String> emptyStream = Stream.empty();
		assertEquals(0, emptyStream.count());

		// create an stream with values
		Stream<String> streamOfArray = Stream.of("a", "b", "c");

		// create an stream from an array
		String[] arr = new String[] { "a", "b", "c" };
		Stream<String> streamOfArrayFull = Arrays.stream(arr);

		// create an stream from part of an array
		arr = new String[] { "a", "b", "c", "d", "f" };
		Stream<String> streamOfArrayPart = Arrays.stream(arr, 0, 3);

		// create an stream from a stream builder
		Stream<String> streamBuilder = Stream.<String>builder().add("a").add("b").add("c").build();

		// all are equals
		assertArrayEquals(streamOfArray.toArray(), streamOfArrayFull.toArray());
		assertArrayEquals(streamOfArrayPart.toArray(), streamBuilder.toArray());

		// create an stream with 10 equals elements
		Stream<String> streamGenerated = Stream.generate(() -> "value").limit(10);
		assertEquals(10, streamGenerated.filter(s -> "value".equals(s)).count());

		// create an stream with iterator
		Stream<Integer> streamPairNumbers = Stream.iterate(2, n -> n + 2).limit(10);
		assertArrayEquals(streamPairNumbers.toArray(), new Integer[] { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20 });

		// range not include end range
		IntStream intStream = IntStream.range(1, 3);
		assertArrayEquals(intStream.toArray(), new int[] { 1, 2 });

		// rangeClosed include end range
		LongStream longStream = LongStream.rangeClosed(1, 3);
		assertArrayEquals(longStream.toArray(), new long[] { 1, 2, 3 });
	}

	@Test
	void onlyCanBeOperatedOrClosedOnceTest() {
		Stream<String> streamOfArray = Stream.of("a", "b", "c");
		streamOfArray.filter(v -> "a".equals(v));

		// the stream was operated or closed and no intermediate operations can be
		// executed
		assertThrows(IllegalStateException.class, () -> {
			streamOfArray.skip(1);
		});

		// the stream was operated or closed and no terminal operations can be executed
		assertThrows(IllegalStateException.class, () -> {
			streamOfArray.allMatch(v -> v.length() == 1);
		});
	}

	@Test
	void matchTest() {
		Supplier<Stream<String>> streamSupplier = () -> Stream.of("a", "b", "c");

		assertTrue(streamSupplier.get().anyMatch(s -> "a".equals(s)));
		assertTrue(streamSupplier.get().noneMatch(s -> "d".equals(s)));
	}

	@Test
	void findTest() {
		Stream<String> emptyStream = Stream.empty();
		Stream<String> streamValue = Stream.of("a", "b", "c");

		assertFalse(emptyStream.findAny().isPresent());
		assertEquals("a", streamValue.findFirst().orElse("none"));
	}

	@Test
	void lazyInvocationTest() {
		String[] vector = { "" };
		Supplier<Stream<String>> streamSupplier = () -> Stream.of("a", "b", "c");

		// the operation is not executed
		streamSupplier.get().skip(1).filter(v -> {
			vector[0] = "";
			return v.length() == 1;
		});
		assertEquals("", vector[0]);

		// the operation is executed
		streamSupplier.get().skip(1).filter(v -> {
			vector[0] = "default";
			return v.length() == 1;
		}).count();
		assertEquals("default", vector[0]);
	}

	@Test
	void sortedTest() {
		Supplier<Stream<Integer>> intStreamSupplier = () -> Stream.of(2, 8, 1);

		assertEquals(1, intStreamSupplier.get().sorted().findFirst().orElse(0).intValue());
		assertEquals(8, intStreamSupplier.get().sorted(Comparator.reverseOrder()).findFirst().orElse(0).intValue());
	}

	@Test
	void skipTest() {
		Supplier<Stream<Integer>> intStreamSupplier = () -> Stream.of(1, 2, 3, 4, 5, 6);
		Supplier<Stream<Integer>> intStreamSupplierShort = () -> Stream.of(4, 5, 6);

		assertArrayEquals(intStreamSupplierShort.get().toArray(), intStreamSupplier.get().skip(3).toArray());

		assertThrows(IllegalArgumentException.class, () -> intStreamSupplier.get().skip(-2));
	}

	@Test
	void peekTest() {
		Stream<Integer> intStream = Stream.of(1, 2, 3, 4, 5, 6);
		StringBuilder sb = new StringBuilder();

		intStream.peek(value -> sb.append(value)).count();
		assertEquals("123456", sb.toString());
	}

	@Test
	void distinctTest() {
		Stream<Integer> intStream = Stream.of(1, 2, 2, 3, 3, 3);
		StringBuilder sb = new StringBuilder();

		// delete duplicated values
		intStream.distinct().forEach(value -> sb.append(value));
		assertEquals("123", sb.toString());
	}

	@Test
	void mapAndFlatMapTest() {
		Supplier<Stream<String>> stream = () -> Stream.of("1", "2", "3", "4", "5", "6");
		Stream<Company> companies = Stream.of(new Company(new String[] { "Luis", "Jhon" }),
				new Company(new String[] { "Carlos", "Sergio" }), new Company(new String[] { "Fernando" }));

		IntStream numbers = stream.get().mapToInt(Integer::parseInt);
		assertArrayEquals(numbers.toArray(), new int[] { 1, 2, 3, 4, 5, 6 });

		Stream<String> words = stream.get().map(v -> "N� ".concat(v));
		assertArrayEquals(words.toArray(), new String[] { "N� 1", "N� 2", "N� 3", "N� 4", "N� 5", "N� 6" });

		Stream<String> employees = companies.flatMap(company -> company.getEmployees());
		assertArrayEquals(employees.toArray(), new String[] { "Luis", "Jhon", "Carlos", "Sergio", "Fernando" });
	}

	@Test
	void reduceTest() {
		Stream<Integer> intStream = Stream.of(1, 2, 6, 7, 3, 4, 5, 8, 9);
		int major = intStream.reduce((a, b) -> a > b ? a : b).orElse(0);

		assertEquals(9, major);
	}

}
