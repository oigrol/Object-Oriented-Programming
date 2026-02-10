package it.polito.oop.test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import diet.Food;
import diet.Menu;
import diet.Restaurant;
import diet.Takeaway;

class TestR5_Restaurant {

    private Food food;
    private Takeaway takeaway;

    @BeforeEach
    void setUp() {
        food = new Food();
        takeaway = new Takeaway();
    }

    @Test
    void testRestaurantGetName() {
        Restaurant r = takeaway.addRestaurant("Napoli");
        assertNotNull(r, "No restaurant created");
        assertNotNull(r.getName(), "Missing restaurant name");
        assertEquals("Napoli", r.getName(), "Wrong restaurant name");
    }

    @Test
    void testOpenSingle(){
        Restaurant r = takeaway.addRestaurant("Venezia");
        r.setHours("08:00","20:00");

        assertTrue(r.isOpenAt("12:00"), "Should be open at noon");
        assertFalse(r.isOpenAt("21:00"), "Should be close at 9pm");
    }

    @Test
    void testOpenWPause(){
        Restaurant r = takeaway.addRestaurant("Venezia");
        r.setHours("11:00","14:30","19:00","23:00");

        assertTrue(r.isOpenAt("12:00"), "Should be open at noon");
        assertFalse(r.isOpenAt("17:00"), "Should be close at 5pm");
        assertTrue(r.isOpenAt("21:00"), "Should be open at 9pm");
    }

    @Test
    void testRestaurantCreateMenu() {
        Restaurant r = takeaway.addRestaurant("Napoli");
        Menu m = food.createMenu("M1");
        r.addMenu(m);

        assertNotNull(m, "Missing created menu");
        assertSame(m, r.getMenu("M1"), "Retrieved wrong menu");
    }

    @Test
    void testTakeawayAddRestaurant() {
        takeaway.addRestaurant("Napoli");
        takeaway.addRestaurant("Torino");

        Collection<String> restaurants = takeaway.restaurants();

        assertNotNull(restaurants, "Missing restaurants");
        assertEquals(2, restaurants.size(), "Missing restaurant");
        assertTrue(restaurants.contains("Napoli"));
        assertTrue(restaurants.contains("Torino"));
    }
}