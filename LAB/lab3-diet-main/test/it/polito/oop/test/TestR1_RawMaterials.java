package it.polito.oop.test;

import static it.polito.oop.test.CollectionsAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import diet.*;
import java.util.Collection;

class TestR1_RawMaterials {
	private Food food = new Food();

	@BeforeEach
	void setUp() {
		food = new Food();
	}

	@Test
	void testDefinition() {
		int initialSize = food.rawMaterials().size();
		food.defineRawMaterial("Pasta", 350, 12, 72.2, 1.5);
		int finalSize = food.rawMaterials().size();

		assertEquals(0, initialSize, "Initially there should be no raw material");
		assertEquals(1, finalSize, "Expecting one product");
	}

	@Test
	void testRawMaterialsCollection() {
		food.defineRawMaterial("Pasta", 350, 12, 72.2, 1.5);

		Collection<NutritionalElement> c = food.rawMaterials();

		NutritionalElement en = c.iterator().next();

		assertEquals("Pasta", en.getName(), "Wrong material name");
		assertEquals(350, en.getCalories(), 0.001, "Wrong material calories");
		assertEquals(12, en.getProteins(), 0.001, "Wrong material proteins");
		assertEquals(72.2, en.getCarbs(), 0.001, "Wrong material carbs");
		assertEquals(1.5, en.getFat(), 0.001, "Wrong material fat");
		assertTrue(en.per100g(), "Material should report value per 100 grams");
	}

	@Test
	void testRawMaterials() {
		food.defineRawMaterial("Pasta", 350, 12, 72.2, 1.5);
		NutritionalElement en = food.getRawMaterial("Pasta");

		assertEquals("Pasta", en.getName(), "Wrong material name");
		assertEquals(350, en.getCalories(), 0.001, "Wrong material calories");
		assertEquals(12, en.getProteins(), 0.001, "Wrong material proteins");
		assertEquals(72.2, en.getCarbs(), 0.001, "Wrong material carbs");
		assertEquals(1.5, en.getFat(), 0.001, "Wrong material fat");
		assertTrue(en.per100g(), "Material should report value per 100 grams");
	}

	@Test
	void testRawMaterialsCollectionsSorted() {
		food.defineRawMaterial("Zucchero", 400, 0, 100, 0);
		food.defineRawMaterial("Mais", 70, 2.7, 12, 1.3);
		food.defineRawMaterial("Pasta", 350, 12, 72.2, 1.5);

		assertSorted("Raw materials are not sorted",
					 food.rawMaterials(),NutritionalElement::getName);
	}

}
