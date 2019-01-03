package function;

import java.util.Optional;

public class Country {

	public Country(String name, boolean sea) {
		this.name = Optional.ofNullable(name);
		this.sea = Optional.ofNullable(sea);
	}

	private Optional<String> name;

	public Optional<String> getName() {
		return name;
	}

	public void setName(String name) {
		this.name =Optional.ofNullable(name);
	}

	private Optional<Boolean> sea;

	public Optional<Boolean> haveSea() {
		return sea;
	}

	public void setSea(boolean sea) {
		this.sea = Optional.ofNullable(sea);
	}
}
