package it.polito.oop.test;

import static it.polito.oop.test.CollectionsAssertions.assertSorted;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import diet.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestR8_Information {

    private Takeaway t;
    private Restaurant r;
    private Customer u;
    private Customer u2;

    @BeforeEach
    void setUp() {
        Food food = new Food();
        t = new Takeaway();
        r = t.addRestaurant("Napoli");
        r.setHours("08:15", "14:00", "19:00", "23:59");
        Menu m1 = food.createMenu("M1");
        Menu m2 = food.createMenu("M2");
        r.addMenu(m1);
        r.addMenu(m2);
        Restaurant r2 = t.addRestaurant("Milano");
        r2.setHours("08:15", "12:00", "19:00", "23:59");
        u = t.registerCustomer("Marco", "Rossi", "marco.rossi@example.com", "123456789");
        u2 = t.registerCustomer("Giovanni", "Rossi", "giovanni.rossi@example.com", "123456789");
    }

    @Test
    void testOpenedRestaurants() {
        Collection<Restaurant> open = t.openRestaurants("08:15");
        assertNotNull(open, "Missing open restaurant list");
        assertEquals(2, open.size(), "Wrong number of open restaurants");
        assertSorted("Open restaurants should be in alphabetic order", open, Restaurant::getName);
    }

    @Test
    void testOpenedRestaurantsClosing() {
        Collection<Restaurant> open = t.openRestaurants("12:10");
        assertNotNull(open, "Missing open restaurant list");
        assertEquals(1, open.size());
        assertEquals(r.getName(), open.iterator().next().getName());
    }

    @Test
    void testOpenedRestaurantsEmpty() {
        Collection<Restaurant> open = t.openRestaurants("15:00");
        assertNotNull(open, "Missing open restaurant list");
        assertEquals(0, open.size(), "Wrong number of open restaurants");
    }

    @Test
    void testOrdersWithStatus() {
        Order o = t.createOrder(u, "Napoli", "9:00");
        assertNotNull(o, "Missing order");
        o.addMenus("M2", 2);
        o.addMenus("M1", 1);
        t.createOrder(u2, "Napoli", "9:00").addMenus("M1", 1);
        t.createOrder(u, "Napoli", "8:59").addMenus("M1", 3);
        t.createOrder(u, "Napoli", "9:00").addMenus("M1", 1).setStatus(Order.OrderStatus.READY);

        assertEquals("""
                        Napoli, Giovanni Rossi : (09:00):
                        \tM1->1
                        Napoli, Marco Rossi : (08:59):
                        \tM1->3
                        Napoli, Marco Rossi : (09:00):
                        \tM1->1
                        \tM2->2""",
                r.ordersWithStatus(Order.OrderStatus.ORDERED).trim());
    }
}