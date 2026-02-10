package diet;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Facade class for the diet management.
 * It allows defining and retrieving raw materials and products.
 *
 */
public class Food {

	//treeMap contiene già le chiavi (nomi cibi) ordinate in ordine alfabetico
	//rawMaterials, products, recipes implementano tutti NutritionalElement, quindi li generalizzo con questo tipo
	private Map<String, NutritionalElement> rawMaterialsMap = new TreeMap<>();
	private Map<String, NutritionalElement> productsMap = new TreeMap<>();
	private Map<String, NutritionalElement> recipesMap = new TreeMap<>();
	private Map<String, NutritionalElement> menuMap = new TreeMap<>();

	/**
	 * Define a new raw material.
	 * The nutritional values are specified for a conventional 100g quantity
	 * @param name unique name of the raw material
	 * @param calories calories per 100g
	 * @param proteins proteins per 100g
	 * @param carbs carbs per 100g
	 * @param fat fats per 100g
	 */
	public void defineRawMaterial(String name, double calories, double proteins, double carbs, double fat) {
		//assumo nome materie prime univoco
		//parametri si riferiscono a 100 g di materia prima
		if (name == null) {
			throw new IllegalArgumentException("Nome non valido.");
		}
		if (calories < 0 || proteins < 0 || carbs < 0 || fat < 0) {
			throw new IllegalArgumentException("Parametro non valido.");
		}
		RawMaterial rawMaterial = new RawMaterial(name, calories, proteins, carbs, fat);
		rawMaterialsMap.put(name, rawMaterial);
	}

	/**
	 * Retrieves the collection of all defined raw materials
	 * @return collection of raw materials though the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> rawMaterials() {
		//ritorna collection con valori della mappa già ordinati in ordine
		//alfabetico essendo una treeMap con ordinamento per chiave (nome materia prima)
		return rawMaterialsMap.values();
	}

	/**
	 * Retrieves a specific raw material, given its name
	 * @param name  name of the raw material
	 * @return  a raw material though the {@link NutritionalElement} interface
	 */
	public NutritionalElement getRawMaterial(String name) {
		return rawMaterialsMap.get(name);
		/* era RINDONDANTE fare: (get restituisce già null se mappa vuota)
		 * if (rawMaterialsMap.containsKey(name)) {
		 * 	return rawMaterialsMap.get(name);
		 * }
		 * return null;
		 */
	}

	/**
	 * Define a new packaged product.
	 * The nutritional values are specified for a unit of the product
	 * @param name unique name of the product
	 * @param calories calories for a product unit
	 * @param proteins proteins for a product unit
	 * @param carbs carbs for a product unit
	 * @param fat fats for a product unit
	 */
	public void defineProduct(String name, double calories, double proteins, double carbs, double fat) {
		if (name == null) {
			throw new IllegalArgumentException("Nome non valido.");
		}
		if (calories < 0 || proteins < 0 || carbs < 0 || fat < 0) {
			throw new IllegalArgumentException("Parametro non valido.");
		}
		Product product = new Product(name, calories, proteins, carbs, fat);
		productsMap.put(name, product); 
	}

	/**
	 * Retrieves the collection of all defined products
	 * @return collection of products though the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> products() {
		return productsMap.values();
	}

	/**
	 * Retrieves a specific product, given its name
	 * @param name  name of the product
	 * @return  a product though the {@link NutritionalElement} interface
	 */
	public NutritionalElement getProduct(String name) {
		return productsMap.get(name);
	}

	/**
	 * Creates a new recipe stored in this Food container.
	 *  
	 * @param name name of the recipe
	 * @return the newly created Recipe object
	 */
	public Recipe createRecipe(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Nome non valido.");
		}
		Recipe recipe = new Recipe(name, this);
		//gli passo i cibi (materie prime) di questa istanza 
		//per recuperare poi i valori nutrizionali
		recipesMap.put(name, recipe);
		return recipe;
	}
	
	/**
	 * Retrieves the collection of all defined recipes
	 * @return collection of recipes though the {@link NutritionalElement} interface
	 */
	public Collection<NutritionalElement> recipes() {
		return recipesMap.values();
		//restituisce collezione ricette in ordine alfabetico
	}

	/**
	 * Retrieves a specific recipe, given its name
	 * @param name  name of the recipe
	 * @return  a recipe though the {@link NutritionalElement} interface
	 */
	public NutritionalElement getRecipe(String name) {
		return recipesMap.get(name);
	}

	/**
	 * Creates a new menu
	 * 
	 * @param name name of the menu
	 * @return the newly created menu
	 */
	public Menu createMenu(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Nome non valido.");
		}
		Menu menu = new Menu(name, this);
		menuMap.put(name, menu);
		return menu;
	}
}