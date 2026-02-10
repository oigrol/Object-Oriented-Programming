package it.polito.oop.test;

import static it.polito.oop.test.CollectionsAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import diet.*;

class TestR3_Recipes {
    private Food food;

    @BeforeEach
    void setUp() {
        food = new Food();
        food.defineRawMaterial("Zucchero", 400, 0, 100, 0);
        food.defineRawMaterial("Mais", 70, 2.7, 12, 1.3);
        food.defineRawMaterial("Pasta", 350, 12, 72.2, 1.5);
        food.defineRawMaterial("Olio", 900, 0, 0, 100);
        food.defineRawMaterial("Nutella", 530, 6.8, 56, 31);
    }

    @Test
    void testCreateRecipe() {
        /* Recipe r = */ food.createRecipe("Pasta col Mais");

        assertNotNull(food.getRecipe("Pasta col Mais"), "Missing recipe");

        assertNotNull(food.recipes(), "Missing recipes");
        assertEquals(1, food.recipes().size(), "Wrong number of recipes");
    }

    @Test
    void testCreateManyRecipe() {
        food.createRecipe("Pasta alla Norma");
        food.createRecipe("Melanzane alla Parmigiana");
        food.createRecipe("Bistecca alla Fiorentina");
        food.createRecipe("Tiramisu'");

        assertNotNull(food.getRecipe("Tiramisu'"), "Missing recipe");
        assertNotNull(food.recipes(), "Missing recipes");
        assertEquals(4, food.recipes().size(), "Wrong number of recipes");
    }

    @Test
    void testRecipesOrder() {
        food.createRecipe("Pasta alla Norma");
        food.createRecipe("Melanzane alla Parmigiana");
        food.createRecipe("Bistecca alla Fiorentina");
        food.createRecipe("Tiramisu'");
        food.createRecipe("Valdostana");
        food.createRecipe("Ratatouille");
        food.createRecipe("Bagna caoda");

        Collection<NutritionalElement> recipes = food.recipes();
        assertNotNull(recipes);
        assertEquals(7, recipes.size(), "Some recipes missing");
        assertSorted("Wrong recipes order", recipes,NutritionalElement::getName);
    }

    @Test
    void testRecipe() {

        Recipe r = food.createRecipe("Pasta e Nutella");

        r.addIngredient("Pasta", 70);
        r.addIngredient("Nutella", 30);
        assertEquals(350 * 0.7 + 530 * 0.3, r.getCalories(), 0.001, "Wrong recipe calories value");
        assertEquals(12 * 0.7 + 6.8 * 0.3, r.getProteins(), 0.001, "Wrong recipe proteins value");
        assertEquals(72.2 * 0.7 + 56 * 0.3, r.getCarbs(), 0.001, "Wrong recipe carbs value");
        assertEquals(1.5 * 0.7 + 31 * 0.3, r.getFat(), 0.001, "Wrong recipe fat value");
        assertTrue(r.per100g());
    }

    @Test
    void testRecipe2() {
        Recipe r = food.createRecipe("Pasta col Mais");

        r.addIngredient("Pasta", 70);
        r.addIngredient("Mais", 70);
        r.addIngredient("Olio", 13);

        assertEquals((350 * 0.7 + 70 * 0.7 + 900 * 0.13) * 100 / 153, r.getCalories(),
                0.001, "Wrong recipe calories value");
        assertEquals((12 * 0.7 + 2.7 * 0.7 + 0 * 0.13) * 100 / 153, r.getProteins(),
                0.001, "Wrong recipe proteins value");
        assertEquals((72.2 * 0.7 + 12 * 0.7 + 0 * 0.13) * 100 / 153, r.getCarbs(), 0.001, "Wrong recipe carbs value");
        assertEquals((1.5 * 0.7 + 1.3 * 0.7 + 100 * 0.13) * 100 / 153, r.getFat(), 0.001, "Wrong recipe fat value");
    }

}