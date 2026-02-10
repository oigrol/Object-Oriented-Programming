package diet;

import java.util.*;


/**
 * Represents a takeaway restaurant chain.
 * It allows managing restaurants, customers, and orders.
 */
public class Takeaway {
	private Map<String, Restaurant> restaurantMap = new TreeMap<>();
	private Map<String, Customer> customerMap = new TreeMap<>();

	/**
	 * Creates a new restaurant with a given name
	 *
	 * @param restaurantName name of the restaurant
	 * @return the new restaurant
	 */
	public Restaurant addRestaurant(String restaurantName) {
		if (restaurantName == null) {
			throw new IllegalArgumentException("Nome non valido.");
		}
		Restaurant restaurant = new Restaurant(restaurantName);
		restaurantMap.put(restaurantName, restaurant);
		return restaurant;
	}

	/**
	 * Retrieves the names of all restaurants
	 *
	 * @return collection of restaurant names
	 */
	public Collection<String> restaurants() {
		return restaurantMap.keySet();
	}

	/**
	 * Creates a new customer for the takeaway
	 * @param firstName first name of the customer
	 * @param lastName	last name of the customer
	 * @param email		email of the customer
	 * @param phoneNumber mobile phone number
	 *
	 * @return the object representing the newly created customer
	 */
	public Customer registerCustomer(String firstName, String lastName, String email, String phoneNumber) {
		if (firstName == null || lastName == null || email == null || phoneNumber == null) {
			throw new IllegalArgumentException("Nome non valido.");
		}
		Customer customer = new Customer(firstName, lastName, email, phoneNumber);
		customerMap.put(lastName + firstName, customer);
		return customer;
	}

	/**
	 * Retrieves all registered customers
	 *
	 * @return sorted collection of customers
	 */
	public Collection<Customer> customers(){
		return customerMap.values();
	}


	/**
	 * Creates a new order for the chain.
	 *
	 * @param customer		 customer issuing the order
	 * @param restaurantName name of the restaurant that will take the order
	 * @param time	time of desired delivery
	 * @return order object
	 */
	public Order createOrder(Customer customer, String restaurantName, String time) {
		// time = "HH:MM"
		if (customer == null || restaurantName == null || time == null) {
			throw new IllegalArgumentException("Parametri non validi.");
		}

		time = correctFormatTime(time); //salvo orario nel formato corretto HH:MM

		Restaurant restaurant = restaurantMap.get(restaurantName);
		if (restaurant.isOpenAt(time) == false) {
			//se orario di consegna fuori da orario di servizio ristorante
			//orario di consegna è impostato alla prima ora di apertura successiva
			time = restaurant.findAvailableTime(time);
		}
		Order order = new Order(customer, restaurantName, time);
		restaurant.addOrder(order);
		return order;
	}

	/**
	 * Find all restaurants that are open at a given time.
	 *
	 * @param time the time with format {@code "HH:MM"}
	 * @return the sorted collection of restaurants
	 */
	public Collection<Restaurant> openRestaurants(String time){
		Collection<Restaurant> restaurants = new LinkedList<>();
		for (Restaurant restaurant : restaurantMap.values()) {
			//restaurantMap è già ordinato alfabeticamente per nome
			if (restaurant.isOpenAt(time)) {
				restaurants.add(restaurant);
			}
		}
		return restaurants;
	}

	//orario deve essere nel formato HH:MM
	private String correctFormatTime(String time) {
        if (time.indexOf(':') == 1) { // Se ho es: "9:00", ovvero : è al secondo carattere 
            return "0" + time; //lo faccio diventare "09:00"
        }
        return time; // es: "10:00"
    }
}
