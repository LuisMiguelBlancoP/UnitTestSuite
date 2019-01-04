package stream;

import java.util.stream.Stream;

public class Company {

	Stream<String> employees = Stream.empty();

	public Company(String[] employee) {
		this.employees = Stream.of(employee);
	}

	public Stream<String> getEmployees() {
		return employees;
	}

}
