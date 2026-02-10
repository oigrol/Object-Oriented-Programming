package it.polito.oop.test;

import static org.junit.jupiter.api.Assertions.*;

import diet.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import diet.Customer;

public class TestR7_Orders {

    public static final String RESTAURANT_NAME = "Napoli";
    private Takeaway t;
    private Customer u;

    @BeforeEach
    void setUp() {
        Food food = new Food();
        t = new Takeaway();
        Restaurant r = t.addRestaurant(RESTAURANT_NAME);
        r.setHours("08:15", "14:00", "19:00", "23:55");
        Menu m1 = food.createMenu("M1");
        Menu m2 = food.createMenu("M2");
        r.addMenu(m1);
        r.addMenu(m2);

        u = t.registerCustomer("Marco", "Rossi", "marco.rossi@example.com", "123456789");
    }

    @Test
    void testTakeawayCreateOrder() {
        Order o = t.createOrder(u, RESTAURANT_NAME, "19:10");
        assertNotNull(o, "Missing order");
        assertEquals(Order.OrderStatus.ORDERED, o.getStatus());
        assertEquals(Order.PaymentMethod.CASH, o.getPaymentMethod());
    }

    @Test
    void testOrderToStringWorkingTime() {
        Order o = t.createOrder(u, RESTAURANT_NAME, "09:30");
        assertNotNull(o, "Missing order");
        assertEquals("Napoli, Marco Rossi : (09:30):", o.toString().trim());
    }

    @Test
    void testOrderToStringOpenTime() {
        Order o = t.createOrder(u, RESTAURANT_NAME, "19:10");
        assertNotNull(o, "Missing order");
        assertEquals("Napoli, Marco Rossi : (19:10):", o.toString().trim());
    }

    @Test
    void testOrderToStringInvalidTime() {
        Order o = t.createOrder(u, RESTAURANT_NAME, "15:30");
        assertNotNull(o, "Missing order");
        assertEquals("Napoli, Marco Rossi : (19:00):", o.toString().trim());
    }

    @Test
    void testOrderSetStatus() {
        Order o = t.createOrder(u, RESTAURANT_NAME, "19:10");
        assertNotNull(o, "Missing order");
        o.setStatus(Order.OrderStatus.DELIVERED);
        assertEquals(Order.OrderStatus.DELIVERED, o.getStatus());
    }

    @Test
    void testOrderSetPaymentMethod() {
        Order o = t.createOrder(u, RESTAURANT_NAME, "19:10");
        assertNotNull(o, "Missing order");
        o.setPaymentMethod(Order.PaymentMethod.PAID);
        assertEquals(Order.PaymentMethod.PAID, o.getPaymentMethod());
    }

    @Test
    void testOrderAddMenus() {
        Order o = t.createOrder(u, RESTAURANT_NAME, "9:00");
        assertNotNull(o, "Missing order");
        o.addMenus("M1", 1);
        o.addMenus("M2", 2);
        assertEquals("Napoli, Marco Rossi : (09:00):\n\tM1->1\n\tM2->2", o.toString().trim());
    }

    @Test
    void testOrderAddMenusOverwrite() {
        Order o = t.createOrder(u, RESTAURANT_NAME, "9:00");
        assertNotNull(o, "Missing order");
        o.addMenus("M1", 1);
        o.addMenus("M2", 2);
        o.addMenus("M1", 3);
        assertEquals("Napoli, Marco Rossi : (09:00):\n\tM1->3\n\tM2->2", o.toString().trim());
    }

    @Test
    void testOrderAddMenusSorted() {
        Order o = t.createOrder(u, RESTAURANT_NAME, "9:00");
        assertNotNull(o, "Missing order");
        o.addMenus("M2", 2);
        o.addMenus("M1", 1);
        assertEquals("Napoli, Marco Rossi : (09:00):\n\tM1->1\n\tM2->2", o.toString().trim());
    }
}