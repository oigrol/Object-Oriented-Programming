package mountainhuts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.smartcardio.CommandAPDU;

/**
 * Class {@code Region} represents the main facade
 * class for the mountains hut system.
 * 
 * It allows defining and retrieving information about
 * municipalities and mountain huts.
 *
 */
public class Region {
	private String name;
	private List<AltitudeRange> altitudeRanges = new ArrayList<>();
	private Map<String, Municipality> municipalityMap = new HashMap<>(); 
	private Map<String, MountainHut> mountainHutMap = new HashMap<>(); 

	/**
	 * Create a region with the given name.
	 * 
	 * @param name
	 *            the name of the region
	 */
	public Region(String name) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Nome mancante");
		}
		this.name = name;
	}

	/**
	 * Return the name of the region.
	 * 
	 * @return the name of the region
	 */
	public String getName() {
		return name;
	}

	/**
	 * Create the ranges given their textual representation in the format
	 * "[minValue]-[maxValue]".
	 * 
	 * @param ranges
	 *            an array of textual ranges
	 */
	public void setAltitudeRanges(String... ranges) {
		for (String string : ranges) {
			String[] subString = string.split("-");
			int min = Integer.parseInt(subString[0]);
			int max = Integer.parseInt(subString[1]);
			AltitudeRange range = new AltitudeRange(min, max);
			altitudeRanges.add(range);
		}
	}

	/**
	 * Return the textual representation in the format "[minValue]-[maxValue]" of
	 * the range including the given altitude or return the default range "0-INF".
	 * 
	 * @param altitude
	 *            the geographical altitude
	 * @return a string representing the range
	 */
	public String getAltitudeRange(Integer altitude) {
		for (AltitudeRange altitudeRange : altitudeRanges) {
			if (altitudeRange.findAltitudeRange(altitude)) {
				return altitudeRange.toString();
			}
		}
		return "0-INF";
	}

	/**
	 * Return all the municipalities available.
	 * 
	 * The returned collection is unmodifiable
	 * 
	 * @return a collection of municipalities
	 */
	public Collection<Municipality> getMunicipalities() {
		return municipalityMap.values();
	}

	/**
	 * Return all the mountain huts available.
	 * 
	 * The returned collection is unmodifiable
	 * 
	 * @return a collection of mountain huts
	 */
	public Collection<MountainHut> getMountainHuts() {
		return mountainHutMap.values();
	}

	/**
	 * Create a new municipality if it is not already available or find it.
	 * Duplicates must be detected by comparing the municipality names.
	 * 
	 * @param name
	 *            the municipality name
	 * @param province
	 *            the municipality province
	 * @param altitude
	 *            the municipality altitude
	 * @return the municipality
	 */
	public Municipality createOrGetMunicipality(String name, String province, Integer altitude) {
		Municipality municipality = municipalityMap.get(name);
		if (municipality == null) {
			municipality = new Municipality(name, province, altitude);
			municipalityMap.put(name, municipality);
		}
		return municipality;
	}

	/**
	 * Create a new mountain hut if it is not already available or find it.
	 * Duplicates must be detected by comparing the mountain hut names.
	 *
	 * @param name
	 *            the mountain hut name
	 * @param category
	 *            the mountain hut category
	 * @param bedsNumber
	 *            the number of beds in the mountain hut
	 * @param municipality
	 *            the municipality in which the mountain hut is located
	 * @return the mountain hut
	 */
	public MountainHut createOrGetMountainHut(String name, String category, 
											  Integer bedsNumber, Municipality municipality) {
		return createOrGetMountainHut(name, null, category, bedsNumber, municipality);
	}

	/**
	 * Create a new mountain hut if it is not already available or find it.
	 * Duplicates must be detected by comparing the mountain hut names.
	 * 
	 * @param name
	 *            the mountain hut name
	 * @param altitude
	 *            the mountain hut altitude
	 * @param category
	 *            the mountain hut category
	 * @param bedsNumber
	 *            the number of beds in the mountain hut
	 * @param municipality
	 *            the municipality in which the mountain hut is located
	 * @return a mountain hut
	 */
	public MountainHut createOrGetMountainHut(String name, Integer altitude, String category, 
											  Integer bedsNumber, Municipality municipality) {
		MountainHut mountainHut = mountainHutMap.get(name);
		if (mountainHut == null) {
			mountainHut = new MountainHut(name, altitude, category, bedsNumber, municipality);
			mountainHutMap.put(name, mountainHut);
		}
		return mountainHut;
	}

	/**
	 * Creates a new region and loads its data from a file.
	 * 
	 * The file must be a CSV file and it must contain the following fields:
	 * <ul>
	 * <li>{@code "Province"},
	 * <li>{@code "Municipality"},
	 * <li>{@code "MunicipalityAltitude"},
	 * <li>{@code "Name"},
	 * <li>{@code "Altitude"},
	 * <li>{@code "Category"},
	 * <li>{@code "BedsNumber"}
	 * </ul>
	 * 
	 * The fields are separated by a semicolon (';'). The field {@code "Altitude"}
	 * may be empty.
	 * 
	 * @param name
	 *            the name of the region
	 * @param file
	 *            the path of the file
	 */
	public static Region fromFile(String name, String file) {
		//check
		Objects.requireNonNull(name);
		Objects.requireNonNull(file);

		Region region = new Region(name);
		List<String> lines = readData(file);
		lines.stream().skip(1)
			.forEach(line -> {
				String[] fields = line.split(";");
				Municipality municipality = region.createOrGetMunicipality(fields[1], fields[0], Integer.parseInt(fields[2]));

				String string = fields[4];
				Integer altitude = (string.equals("")) ? null : Integer.parseInt(string);
				MountainHut mountainHut;
				if (altitude == null) {
					mountainHut = region.createOrGetMountainHut(fields[3], fields[5], Integer.parseInt(fields[6]), municipality);
				} else {
					mountainHut = region.createOrGetMountainHut(fields[3], altitude, fields[5], Integer.parseInt(fields[6]), municipality);
				}
			});
		return region;
	}

	/**
	 * Reads the lines of a text file.
	 *
	 * @param file path of the file
	 * @return a list with one element per line
	 */
	public static List<String> readData(String file) {
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			return in.lines().toList();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * Count the number of municipalities with at least a mountain hut per each
	 * province.
	 * 
	 * @return a map with the province as key and the number of municipalities as
	 *         value
	 */
	public Map<String, Long> countMunicipalitiesPerProvince() {
		return getMunicipalities().stream()
			.collect(Collectors.groupingBy(
				m -> m.getProvince(), Collectors.counting()
			)); //opp: (Municipality::getProvince, Collectors.counting()) <-> (chiave di raggruppamento, cosa faccio per ogni valore raggruppato)
	}

	/**
	 * Count the number of mountain huts per each municipality within each province.
	 * 
	 * @return a map with the province as key and, as value, a map with the
	 *         municipality as key and the number of mountain huts as value
	 */
	public Map<String, Map<String, Long>> countMountainHutsPerMunicipalityPerProvince() {
		//uso raggruppamento a cascata per mettere una mappa nella mappa
		return getMountainHuts().stream()
			.collect(Collectors.groupingBy(
				//mappa esterna
				h -> h.getMunicipality().getProvince(), //chiave 1: provincia
				Collectors.groupingBy(
					//mappa interna
					h -> h.getMunicipality().getName(), Collectors.counting() //chiave 2: città, valore: conta rifugi del comune (di quella provincia)
				)
			));
	}

	/**
	 * Count the number of mountain huts per altitude range. If the altitude of the
	 * mountain hut is not available, use the altitude of its municipality.
	 * 
	 * @return a map with the altitude range as key and the number of mountain huts
	 *         as value
	 */
	public Map<String, Long> countMountainHutsPerAltitudeRange() {
		return getMountainHuts().stream()
			.collect(Collectors.groupingBy(
				h -> {
					Integer altitude = h.getAltitude()
						.orElse(h.getMunicipality().getAltitude());
					return getAltitudeRange(altitude);
				},
				Collectors.counting()
			));
	}

	/**
	 * Compute the total number of beds available in the mountain huts per each
	 * province.
	 * 
	 * @return a map with the province as key and the total number of beds as value
	 */
	public Map<String, Integer> totalBedsNumberPerProvince() {
		return getMountainHuts().stream()
			.collect(Collectors.groupingBy(
				h -> h.getMunicipality().getProvince(),
				Collectors.summingInt(h -> h.getBedsNumber()) //opp: Collectors.summingInt(MountainHut::getBedsNumber)
			));
	}

	/**
	 * Compute the maximum number of beds available in a single mountain hut per
	 * altitude range. If the altitude of the mountain hut is not available, use the
	 * altitude of its municipality.
	 * 
	 * @return a map with the altitude range as key and the maximum number of beds
	 *         as value
	 */
	public Map<String, Optional<Integer>> maximumBedsNumberPerAltitudeRange() {
		return getMountainHuts().stream()
			.collect(Collectors.groupingBy(
				h -> {
					Integer altitude = h.getAltitude()
						.orElse(h.getMunicipality().getAltitude());
					return getAltitudeRange(altitude);
				},
				Collectors.mapping(
					MountainHut::getBedsNumber, //mapping estrae il numero di letti di ogni rifugio
					Collectors.maxBy(Comparator.naturalOrder()) //maxBy prende il massimo tra questi interi
				)
			));
	}

	/**
	 * Compute the municipality names per number of mountain huts in a municipality.
	 * The lists of municipality names must be in alphabetical order.
	 * 
	 * @return a map with the number of mountain huts in a municipality as key and a
	 *         list of municipality names as value
	 */
	public Map<Long, List<String>> municipalityNamesPerCountOfMountainHuts() {
		//conto rifugi per ogni comune
		Map<String, Long> counterHuts = getMountainHuts().stream()
			.collect(Collectors.groupingBy(
				h -> h.getMunicipality().getName(),
				Collectors.counting()
			));
		
		//inverto mappa e salvo i comuni per ogni numero di rifugi
		return counterHuts.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())//valori ordinati alfabeticamente per chiave (nome comune)
			.collect(
				Collectors.groupingBy(
					Map.Entry::getValue,
					Collectors.mapping( //applica un filtro prima di dare risultato a collettore
						Map.Entry::getKey, //dico cosa estrarre -> funzione da applicare a entry in ingresso
						Collectors.toList()	//dico dove mettere ciò che ho estratto -> collettore a cui viene passato risultato
					)
				)
			);
	}

}
