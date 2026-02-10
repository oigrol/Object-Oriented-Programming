package it.polito.oop.test;

import static it.polito.oop.test.CollectionsAssertions.assertSorted;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;

import diet.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import diet.Takeaway;

class TestR6_Users {

    private Takeaway t;

    @BeforeEach
    void setUp(){
        this.t = new Takeaway();
    }

    @Test
    void testTakeawayRegisterUser() {
        Customer u = t.registerCustomer("Marco", "Rossi", "marco.rossi@example.com", "123456789");
        assertNotNull(u, "Missing new registered user");
        assertEquals("Marco", u.getFirstName());
        assertEquals("Rossi", u.getLastName());
        assertEquals("marco.rossi@example.com", u.getEmail());
        assertEquals("123456789", u.getPhone());
    }

    @Test
    void testUserToString() {
        Customer u = t.registerCustomer("Marco", "Rossi", "marco.rossi@example.com", "123456789");
        assertNotNull(u, "Missing new registered user");
        String us = u.toString();
        assertNotNull(us);
        assertFalse(us.contains("@"), "toString method non redefined for User");
        // We split and reorder to allow both "first last" and "last first"
        String[] usa = us.split(" +");
        Arrays.sort(usa);
        assertArrayEquals(new String[] {"Marco","Rossi"}, usa);
    }

    @Test
    void testUserSetEmail() {
        Customer u = t.registerCustomer("Marco", "Rossi", "marco.rossi@example.com", "123456789");
        assertNotNull(u, "Missing new registered user");
        u.SetEmail("marco@example.com");
        assertEquals("marco@example.com", u.getEmail(), "Wrong email");
    }

    @Test
    void testUserSetPhone() {
        Customer u = t.registerCustomer("Marco", "Rossi", "marco.rossi@example.com", "123456789");
        assertNotNull(u, "Missing new registered user");
        u.setPhone("987654321");
        assertEquals("987654321", u.getPhone(), "Wrong mobile");
    }

    @Test
    void testTakeawayUsers() {
        Customer u1 = t.registerCustomer("Giuseppe", "Verdi", "marco.rossi@example.com", "123456789");
        Customer u2 = t.registerCustomer("Marco", "Rossi", "marco.rossi@example.com", "123456789");
        Customer u3 = t.registerCustomer("Giovanni", "Rossi", "giovanni.rossi@example.com", "123456789");
        Collection<Customer> customers = t.customers();
        assertNotNull(customers, "Missing user list");
        assertEquals(3, customers.size(), "Wrong number of users");
        assertTrue(customers.contains(u1), "Missing user in list");
        assertTrue(customers.contains(u2), "Missing user in list");
        assertTrue(customers.contains(u3), "Missing user in list");
    }

    @Test
    void testTakeawayUsersSorted() {
        t.registerCustomer("Giuseppe", "Verdi", "marco.rossi@example.com", "123456789");
        t.registerCustomer("Marco", "Rossi", "marco.rossi@example.com", "123456789");
        t.registerCustomer("Giovanni", "Rossi", "marco.rossi@example.com", "123456789");
        Collection<Customer> customers = t.customers();
        assertNotNull(customers, "Missing user list");
        assertEquals(3, customers.size(), "Wrong number of users");

        assertSorted("Wrong user order", customers, u -> u.getLastName()+u.getFirstName());
    }

}