package diet;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents and order issued by an {@link Customer} for a {@link Restaurant}.
 *
 * When an order is printed to a string is should look like:
 * <pre>
 *  RESTAURANT_NAME, USER_FIRST_NAME USER_LAST_NAME : DELIVERY(HH:MM):
 *  	MENU_NAME_1->MENU_QUANTITY_1
 *  	...
 *  	MENU_NAME_k->MENU_QUANTITY_k
 * </pre>
 */
public class Order implements Comparable<Order>{
	private Customer customer;
	private String restaurantName;
	private String deliveryTime;
	private OrderStatus orderStatus = OrderStatus.ORDERED;
	private PaymentMethod paymentMethod = PaymentMethod.CASH;
	private Map<String, Integer> orderMap = new TreeMap<>();

	public Order(Customer customer, String restaurantName, String deliveryTime) {
		this.customer = customer;
		this.restaurantName = restaurantName;
		this.deliveryTime = deliveryTime;
	}

	/**
	 * Possible order statuses
	 */
	public enum OrderStatus {
		ORDERED, READY, DELIVERED
	}

	/**
	 * Accepted payment methods
	 */
	public enum PaymentMethod {
		PAID, CASH, CARD
	}

	/**
	 * Set payment method
	 * @param pm the payment method
	 */
	public void setPaymentMethod(PaymentMethod pm) {
		paymentMethod = pm;
	}

	/**
	 * Retrieves current payment method
	 * @return the current method
	 */
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * Set the new status for the order
	 * @param os new status
	 */
	public void setStatus(OrderStatus os) {
		orderStatus = os;
	}

	/**
	 * Retrieves the current status of the order
	 *
	 * @return current status
	 */
	public OrderStatus getStatus() {
		return orderStatus;
	}

	/**
	 * Add a new menu to the order with a given quantity
	 *
	 * @param menu	menu to be added
	 * @param quantity quantity
	 * @return the order itself (allows method chaining)
	 */
	public Order addMenus(String menu, int quantity) {
		//Se menu non esiste lo aggiunge.
		//Se menu esiste già, put sovrascrive la quantità definita in precedenza.
		orderMap.put(menu, quantity);
		return this;
	}
	
	@Override
	public String toString() {
		String string = null;
		/*
		* RESTAURANT_NAME, USER_FIRST_NAME USER_LAST_NAME : DELIVERY(HH:MM):
 		* MENU_NAME_1->MENU_QUANTITY_1
 		* ...
 		* MENU_NAME_k->MENU_QUANTITY_k
 		*/
		string = restaurantName + ", " + customer.getFirstName() + " " + customer.getLastName() + 
				" : (" + deliveryTime + "):";

		//se non ci sono menu restituisco solo questa riga
		if (orderMap.isEmpty()) {
            return string;
        }

		//se ci sono menu
		string = string + "\n";

		for (String menu : orderMap.keySet()) {
			int quantity = orderMap.get(menu);
			string = string + "\t" + menu + "->" + quantity + "\n";
		}

		//rimuovo il \n finale
		string = string.substring(0, string.length() - 1);
		
		return string;
	}

	@Override
	public int compareTo(Order o) {
		//ordino per nome ristorante, cognome cliente, nome cliente, orario di consegna
		//this - other
		String thisKeyStringComparable = this.restaurantName + this.customer.getLastName() + this.customer.getFirstName() + this.deliveryTime;
		String otherKeyStringComparable = o.restaurantName + o.customer.getLastName() + o.customer.getFirstName() + o.deliveryTime;

		return thisKeyStringComparable.compareTo(otherKeyStringComparable);
	}
}
