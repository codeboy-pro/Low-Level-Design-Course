package Pomato;

import java.util.List;
import Pomato.models.*;
import Pomato.strategies.*;

public class Main {
  public static void main(String[] args) {
    // Simulating a happy flow
    // Create Pomato_App Object
    Pomato_App pomato = new Pomato_App();

    // Simulate a user coming in (Happy Flow)
    User user = new User(101, "Aditya", "Delhi");
    System.out.println("User: " + user.getName() + " is active.");

    // User searches for restaurants by location
    List<Restaurant> restaurantList = pomato.searchRestaurants("Delhi");

    if (restaurantList.isEmpty()) {
      System.out.println("No restaurants found!");
      return;
    }

    System.out.println("Found Restaurants:");
    for (Restaurant restaurant : restaurantList) {
      System.out.println(" - " + restaurant.getName());
    }

    // User selects a restaurant
    pomato.selectRestaurant(user, restaurantList.get(0));
    System.out.println("Selected restaurant: " + restaurantList.get(0).getName());

    // User adds items to the cart
    pomato.addToCart(user, "P1");
    pomato.addToCart(user, "P2");

    pomato.printUserCart(user);

    // User checkout the cart
    Order order = pomato.checkoutNow(user, "Delivery", new UpiPaymentStrategy("1234567890"));

    // User pays for the cart. If payment is successful, notification is sent.
    pomato.payForOrder(user, order);
  }
}
