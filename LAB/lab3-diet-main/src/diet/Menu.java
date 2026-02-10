package diet;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a complete menu.
 * 
 * It can be made up of both packaged products and servings of given recipes.
 *
 */
public class Menu implements NutritionalElement {
	private String name;
	private Food food;

	private Map<NutritionalElement, Double> menuMap = new HashMap<>();
	//chiave: ricetta o prodotto // valore: grammi o unità

	public Menu(String name, Food food) {
		this.name = name;
		this.food = food;
	}

	/**
	 * Adds a given serving size of a recipe.
	 * The recipe is a name of a recipe defined in the {@code food}
	 * argument of the constructor.
	 * 
	 * @param recipe the name of the recipe to be used as ingredient
	 * @param quantity the amount in grams of the recipe to be used
	 * @return the same Menu to allow method chaining
	 */
    public Menu addRecipe(String recipe, double quantity) {
		NutritionalElement recipeMap = food.getRecipe(recipe);
		if (recipeMap != null) {
			Double oldQuantity = menuMap.get(recipeMap);
			double newQuantity;
			newQuantity = (oldQuantity != null) ? oldQuantity+quantity : quantity;

			menuMap.put(recipeMap, newQuantity);
		}
		return this;
	}

	/**
	 * Adds a unit of a packaged product.
	 * The product is a name of a product defined in the {@code food}
	 * argument of the constructor.
	 * 
	 * @param product the name of the product to be used as ingredient
	 * @return the same Menu to allow method chaining
	 */
    public Menu addProduct(String product) {
		NutritionalElement productMap = food.getProduct(product);
		if (productMap != null) {
			Double oldQuantity = menuMap.get(productMap);
			double newQuantity;
			newQuantity = (oldQuantity != null) ? oldQuantity+1.0 : 1.0;
			menuMap.put(productMap, newQuantity);
		}
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Total KCal in the menu
	 */
	@Override
	public double getCalories() {
		double caloriesTot = 0.0;
		for (NutritionalElement element : menuMap.keySet()) {
			double quantity = menuMap.get(element);
			double calories = 0.0;
			if (element.per100g() == true) {
				//calorie per un grammo
				calories = element.getCalories() / 100.0;
			} else {
				//calorie per un'unità
				calories = element.getCalories();
			}
			caloriesTot += calories * quantity;	
		}
		return caloriesTot;
	}

	/**
	 * Total proteins in the menu
	 */
	@Override
	public double getProteins() {
		double proteinsTot = 0.0;
		for (NutritionalElement element : menuMap.keySet()) {
			double quantity = menuMap.get(element);
			double proteins = 0.0;
			if (element.per100g() == true) {
				//proteine per un grammo
				proteins = element.getProteins() / 100.0;
			} else {
				//proteine per un'unità
				proteins = element.getProteins();
			}
			proteinsTot += proteins * quantity;	
		}
		return proteinsTot;
	}

	/**
	 * Total carbs in the menu
	 */
	@Override
	public double getCarbs() {
		double carbsTot = 0.0;
		for (NutritionalElement element : menuMap.keySet()) {
			double quantity = menuMap.get(element);
			double carbs = 0.0;
			if (element.per100g() == true) {
				//carboidrati per un grammo
				carbs = element.getCarbs() / 100.0;
			} else {
				//carboidrati per un'unità
				carbs = element.getCarbs();
			}
			carbsTot += carbs * quantity;	
		}
		return carbsTot;
	}

	/**
	 * Total fats in the menu
	 */
	@Override
	public double getFat() {
		double fatTot = 0.0;
		for (NutritionalElement element : menuMap.keySet()) {
			double quantity = menuMap.get(element);
			double fat = 0.0;
			if (element.per100g() == true) {
				//grassi per un grammo
				fat = element.getFat() / 100.0;
			} else {
				//grassi per un'unità
				fat = element.getFat();
			}
			fatTot += fat * quantity;	
		}
		return fatTot;
	}

	/**
	 * Indicates whether the nutritional values returned by the other methods
	 * refer to a conventional 100g quantity of nutritional element,
	 * or to a unit of element.
	 * 
	 * For the {@link Menu} class it must always return {@code false}:
	 * nutritional values are provided for the whole menu.
	 * 
	 * @return boolean indicator
	 */
	@Override
	public boolean per100g() {
		return false;
	}
}