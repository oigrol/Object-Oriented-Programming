package diet;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a recipe of the diet.
 * 
 * A recipe consists of a a set of ingredients that are given amounts of raw materials.
 * The overall nutritional values of a recipe can be computed
 * on the basis of the ingredients' values and are expressed per 100g
 * 
 *
 */
public class Recipe implements NutritionalElement {
	private String name;
	private Food food;

	private Map<NutritionalElement, Double> ingredients = new HashMap<>();
	private double quantityTot = 0.0;

	public Recipe(String name, Food food) {
		this.name = name;
		this.food = food;
	}

	//La classe Recipe implementa l'interfaccia NutritionalElement e 
	//i valori nutrizionali sono espressi per 100 grammi.
	
	/**
	 * Adds the given quantity of an ingredient to the recipe.
	 * The ingredient is a raw material.
	 * 
	 * @param material the name of the raw material to be used as ingredient
	 * @param quantity the amount in grams of the raw material to be used
	 * @return the same Recipe object, it allows method chaining.
	 */
	public Recipe addIngredient(String material, double quantity) {
		NutritionalElement rawMaterial = food.getRawMaterial(material);
		//N.B: se materia prima già presente in ingredienti ricetta, viene
		//impostata la nuova quantità (hashmap sovrascrive)
		if (rawMaterial != null) {
			//se sovrascrivo la map ingredients devo sovrascrivere anche la vecchia quantityTot
            Double oldQuantity = ingredients.get(rawMaterial);
            if (oldQuantity != null) {
                quantityTot -= oldQuantity;
            }

			ingredients.put(rawMaterial, quantity);
			quantityTot += quantity;
		}
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	
	@Override
	public double getCalories() {
		if (quantityTot == 0.0) {
			return 0.0;
		}

		double caloriesTot = 0.0;
		/*
		 * avrei potuto anche fare
		 * for (Map.Entry<NutritionalElement, Double> ingredient : ingredients.entrySet()) {
		 * iterando sulla coppia (chiave, valore)
		 */
		for (NutritionalElement rawMaterial : ingredients.keySet()) {
			double quantity = ingredients.get(rawMaterial);
			//la somma delle quantità (in grammi) degli ingredienti di una ricetta non necessariamente è pari a 100g
			caloriesTot += (rawMaterial.getCalories() / 100.0) * quantity; //calorie per un grammo * grammi tot ricetta
			//ottengo calorie totali per il peso totale della ricetta -> le devo scalare a 100g
		}
		//i valori nutrizionali della ricetta devono essere invece riferiti a un'ideale porzione di 100g.
		return (caloriesTot / quantityTot) * 100.0; //ritorno le calorie totali per 100 g
	}

	@Override
	public double getProteins() {
		if (quantityTot == 0.0) {
			return 0.0;
		}

		double proteinsTot = 0.0;
		for (NutritionalElement rawMaterial : ingredients.keySet()) {
			double quantity = ingredients.get(rawMaterial);
			proteinsTot += (rawMaterial.getProteins() / 100.0) * quantity;
		}
		return (proteinsTot / quantityTot) * 100.0;
	}

	@Override
	public double getCarbs() {
		if (quantityTot == 0.0) {
			return 0.0;
		}

		double carbsTot = 0.0;
		for (NutritionalElement rawMaterial : ingredients.keySet()) {
			double quantity = ingredients.get(rawMaterial);
			carbsTot += (rawMaterial.getCarbs() / 100.0) * quantity;
		}
		return (carbsTot / quantityTot) * 100.0;
	}

	@Override
	public double getFat() {
		if (quantityTot == 0.0) {
			return 0.0;
		}

		double fatTot = 0.0;
		for (NutritionalElement rawMaterial : ingredients.keySet()) {
			double quantity = ingredients.get(rawMaterial);
			fatTot += (rawMaterial.getFat() / 100.0) * quantity;
		}
		return (fatTot / quantityTot) * 100.0;
	}

	/**
	 * Indicates whether the nutritional values returned by the other methods
	 * refer to a conventional 100g quantity of nutritional element,
	 * or to a unit of element.
	 * 
	 * For the {@link Recipe} class it must always return {@code true}:
	 * a recipe expresses nutritional values per 100g
	 * 
	 * @return boolean indicator
	 */
	@Override
	public boolean per100g() {
		return true;
	}
	
}
