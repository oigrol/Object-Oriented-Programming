package mountainhuts;

import java.util.Optional;

/**
 * Represents a mountain hut
 * 
 * It includes a name, optional altitude, category,
 * number of beds and location municipality.
 *  
 *
 */
public class MountainHut {
	private String name;
	private Integer altitude;
	private String category;
	private Integer bedsNumber;
	private Municipality municipality;

	public MountainHut(String name, Integer altitude, String category, Integer bedsNumber, Municipality municipality) {
		if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome non valido.");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Categoria non valida.");
        if (bedsNumber == null || bedsNumber < 0) throw new IllegalArgumentException("Numero di posti letto non valida.");
		if (municipality == null) throw new IllegalArgumentException("Città non valida.");
		this.name = name;
		this.altitude = altitude;
		this.category = category;
		this.bedsNumber = bedsNumber;
		this.municipality = municipality;
	}

	/**
	 * Retrieves the name of the hut
	 * @return name of the hut
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves altituted if available
	 * 
	 * @return optional hut altitude
	 */
	public Optional<Integer> getAltitude() {
		return Optional.ofNullable(altitude);
	}

	/**
	 * Retrieves the category of the hut
	 * @return hut category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Retrieves the number of beds available in the hut
	 * @return number of beds
	 */
	public Integer getBedsNumber() {
		return bedsNumber;
	}

	/**
	 * Retrieves the municipality of the hut
	 * @return hut municipality
	 */
	public Municipality getMunicipality() {
		return municipality;
	}
}
