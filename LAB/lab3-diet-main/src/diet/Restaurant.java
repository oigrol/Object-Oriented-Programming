package diet;

import diet.Order.OrderStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a restaurant class with given opening times and a set of menus.
 */
public class Restaurant {
	private String name;
	private Map<String, Menu> menuMap = new TreeMap<>();
	private List<String> times = new ArrayList<>();
	private List<Order> orderList = new ArrayList<>();

	public Restaurant(String name) {
		this.name = name;
	}
	/**
	 * retrieves the name of the restaurant.
	 *
	 * @return name of the restaurant
	 */
	public String getName() {
		return name;
	}

	/**
	 * Define opening times.
	 * Accepts an array of strings (even number of elements) in the format {@code "HH:MM"},
	 * so that the closing hours follow the opening hours
	 * (e.g., for a restaurant opened from 8:15 until 14:00 and from 19:00 until 00:00,
	 * arguments would be {@code "08:15", "14:00", "19:00", "00:00"}).
	 *
	 * @param hm sequence of opening and closing times
	 */
	public void setHours(String ... hm) {
		times.clear();
		//HH:MM
		for (String string : hm) {
			times.add(correctFormatTime(string)); //salvo orario in formato corretto
		}
	}

	/**
	 * Checks whether the restaurant is open at the given time.
	 *
	 * @param time time to check
	 * @return {@code true} is the restaurant is open at that time
	 */
	public boolean isOpenAt(String time){
		//orario deve essere nel formato corretto
		time = correctFormatTime(time);

		for (int i=0; i<times.size(); i=i+2) {
			String openTime = times.get(i);
			String closeTime = times.get(i+1);
			if (openTime.compareTo(closeTime) < 0) {
				//caso Normale
				//se orario di apertura < orario di chiusura -> normale
				if (time.compareTo(openTime) >= 0 && time.compareTo(closeTime) < 0) { 
					//aperto se time >= openTime e time < closeTime
					return true;
				}
			} else {
				//caso Post mezzanotte
				//se orario di apertura >= orario di chiusura -> dopo la mezzanotte
				if (time.compareTo(openTime) >= 0 || time.compareTo(closeTime) < 0) { 
					//aperto se time >= openTime oppure time < closeTime
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Adds a menu to the list of menus offered by the restaurant
	 *
	 * @param menu	the menu
	 */
	public void addMenu(Menu menu) {
		menuMap.put(menu.getName(), menu);
	}

	/**
	 * Gets the restaurant menu with the given name
	 *
	 * @param name	name of the required menu
	 * @return menu with the given name
	 */
	public Menu getMenu(String name) {
		return menuMap.get(name);
	}

	/**
	 * Retrieve all order with a given status with all the relative details in text format.
	 *
	 * @param status the status to be matched
	 * @return textual representation of orders
	 */
	public String ordersWithStatus(OrderStatus status) {
		String string = new String();
		List<Order> ordersFilteredList = new LinkedList<>();
		for (Order order : orderList) {
			if (order.getStatus() == status) {
				ordersFilteredList.add(order);
			}
		}

		//ordino la lista per nome del ristorante, nome dell'utente e orario di consegna
		ordersFilteredList.sort(null);
		for (Order order : ordersFilteredList) {
			string += order.toString();
			string += "\n";
		}
		return string;
	}

	public String findAvailableTime(String time) {
		if (times.isEmpty()) {
			throw new RuntimeException("Orari di apertura ristorante mancanti!");
		}

		time = correctFormatTime(time); //tempo nel formato corretto

		String firstAvailableTime = null;
		//trovo il primo orario di apertura dopo l'orario richiesto		
		for (int i=0; i<times.size(); i+=2) {
			String openTime = times.get(i);
			if (openTime.compareTo(time) > 0) {
				//salvo il primo orario di apertura successivo a quello richiesto
				//se è il primo trovato o se è più vicino a time rispetto a quello già trovato
				if (firstAvailableTime == null || openTime.compareTo(firstAvailableTime) < 0) {
					firstAvailableTime = openTime;
				}
			}
		}

		//primo caso: ho trovato un orario libero in giornata
		if (firstAvailableTime != null) {
			return firstAvailableTime;
		}

		//secondo caso: orario in giornata non trovato. 
		//Prendo il primo orario disponibile del giorno dopo.
		return times.get(0);
	}

	public void addOrder(Order order) {
		orderList.add(order);
	}

	//orario deve essere nel formato HH:MM
	private String correctFormatTime(String time) {
        if (time.indexOf(':') == 1) { // Se ho es: "9:00", ovvero : è al secondo carattere 
            return "0" + time; //lo faccio diventare "09:00"
        }
        return time; // es: "10:00"
    }
}