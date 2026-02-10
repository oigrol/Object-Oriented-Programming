package it.polito.oop.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import diet.*;


public class TestR4_Menu {
    public static final String PASTA_E_NUTELLA = "Pasta e Nutella";
    private Food food;

    @BeforeEach
    void setUp() {
        food = new Food();
        food.defineRawMaterial("Zucchero", 400, 0, 100, 0);
        food.defineRawMaterial("Mais", 70, 2.7, 12, 1.3);
        food.defineRawMaterial("Pasta", 350, 12, 72.2, 1.5);
        food.defineRawMaterial("Olio", 900, 0, 0, 100);
        food.defineRawMaterial("Nutella", 530, 6.8, 56, 31);
        food.defineProduct("Cracker", 111, 2.6, 17.2, 3.5);
        food.defineProduct("Cornetto Cioccolato", 230, 3, 27, 11);
        food.defineProduct("Barretta Bueno", 122, 2, 10.6, 8);
        food.defineProduct("Mozzarella Light", 206, 25, 2, 11.25);

        Recipe r = food.createRecipe(PASTA_E_NUTELLA);
        r.addIngredient("Pasta", 70);
        r.addIngredient("Nutella", 30);
    }

    @Test
    void testMenuWithRecipe() {
        Menu menu = food.createMenu("M1");

        menu.addRecipe(PASTA_E_NUTELLA, 100);

        assertEquals("M1",menu.getName());
        assertEquals(350 * 0.7 + 530 * 0.3, menu.getCalories(), 0.001, "Wrong menu calories value");
        assertEquals(12 * 0.7 + 6.8 * 0.3, menu.getProteins(), 0.001, "Wrong menu proteins value");
        assertEquals(72.2 * 0.7 + 56 * 0.3, menu.getCarbs(), 0.001, "Wrong menu carbs value");
        assertEquals(1.5 * 0.7 + 31 * 0.3, menu.getFat(), 0.001, "Wrong menu fat value");
    }

    @Test
    void testMenu() {
        Menu menu = food.createMenu("M1");

        menu.addRecipe(PASTA_E_NUTELLA, 50);
        menu.addProduct("Cracker");

        assertEquals(350 * 0.35 + 530 * 0.15 + 111, menu.getCalories(), 0.001, "Wrong menu calories value");
        assertEquals(12 * 0.35 + 6.8 * 0.15 + 2.6, menu.getProteins(), 0.001, "Wrong menu proteins value");
        assertEquals(72.2 * 0.35 + 56 * 0.15 + 17.2, menu.getCarbs(), 0.001, "Wrong menu carbs value");
        assertEquals(1.5 * 0.35 + 31 * 0.15 + 3.5, menu.getFat(), 0.001, "Wrong menu fat value");
    }

}