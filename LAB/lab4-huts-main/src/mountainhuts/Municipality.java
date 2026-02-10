package mountainhuts;

/**
 * Class representing a municipality that hosts a mountain hut.
 * It is a data class with getters for name, province, and altitude
 * 
 */
public class Municipality {
	private String name;
	private String province;
	private Integer altitude;

	public Municipality(String name, String province, Integer altitude) {
		if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome non valido.");
        if (province == null || province.isBlank()) throw new IllegalArgumentException("Provincia non valido.");
        if (altitude == null || altitude < 0) throw new IllegalArgumentException("Altitudine non valida.");
		this.name = name;
		this.province = province;
		this.altitude = altitude;
	}

	public String getName() {
		return name;
	}

	public String getProvince() {
		return province;
	}

	public Integer getAltitude() {
		return altitude;
	}

}
