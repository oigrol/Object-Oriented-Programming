package it.polito.oop.test;

import static it.polito.oop.test.CollectionsAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import diet.*;


class TestR2_Products {
    Food food = new Food();

    @BeforeEach
    void setUp() {
        food = new Food();
    }


    @Test
    void testDefinition(){
        int initialSize = food.products().size();
        food.defineProduct("Cracker", 111, 2.6, 17.2, 3.5);
        int finalSize = food.products().size();

        assertEquals(0, initialSize, "Initially there should be no product");
        assertEquals(1, finalSize, "Expecting one product");
    }

    @Test
    void testProductCollection(){
        food.defineProduct("Cracker", 111, 2.6, 17.2, 3.5);

        Collection<NutritionalElement> c = food.products();

        assertNotNull(c, "Missing products");
        NutritionalElement en = c.iterator().next();

        assertEquals("Cracker", en.getName(), "Wrong product name");
        assertEquals(111, en.getCalories(), 0.001, "Wrong product calories value");
        assertEquals(2.6, en.getProteins(), 0.001, "Wrong product proteins value");
        assertEquals(17.2, en.getCarbs(), 0.001, "Wrong product carbs value");
        assertEquals(3.5, en.getFat(), 0.001, "Wrong product fat value");
        assertFalse(en.per100g(), "Values should be per whole product");
    }

    @Test
    void testProduct(){
        food.defineProduct("Cracker", 111, 2.6, 17.2, 3.5);
        NutritionalElement en = food.getProduct("Cracker");

        assertNotNull(en, "Missing product");

        assertEquals("Cracker", en.getName(), "Wrong product name");
        assertEquals(111, en.getCalories(), 0.001, "Wrong product calories value");
        assertEquals(2.6, en.getProteins(), 0.001, "Wrong product proteins value");
        assertEquals(17.2, en.getCarbs(), 0.001, "Wrong product carbs value");
        assertEquals(3.5, en.getFat(), 0.001, "Wrong product fat value");
        assertFalse(en.per100g(), "Values should be per whole product");
    }

    @Test
    void testProductCollectionSorted(){
        food.defineProduct("Cornetto Cioccolato", 230, 3, 27, 11);
        food.defineProduct("Barretta Bueno", 122, 2, 10.6, 8);
        food.defineProduct("Mozzarella Light", 206, 25, 2, 11.25);
        food.defineProduct("Cracker", 111, 2.6, 17.2, 3.5);

        assertSorted("Products are not sorted",
                food.products(),NutritionalElement::getName);
    }

}