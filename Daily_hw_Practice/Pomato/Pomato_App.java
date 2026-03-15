package Pomato;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Pomato.factories.NowOrderFactory;
import Pomato.factories.OrderFactory;
import Pomato.managers.OrderManager;
import Pomato.managers.RestaurantManager;
import Pomato.models.Cart;
import Pomato.models.MenuItem;
import Pomato.models.Order;
import Pomato.models.Restaurant;
import Pomato.models.User;
import Pomato.services.NotificationService;
import Pomato.strategies.PaymentStrategy;

public class Pomato_App {
  private RestaurantManager restaurantManager;
  private OrderManager orderManager;
  private Map<String, MenuItem> menuItems; // itemId -> MenuItem mapping
  private Map<Integer, Restaurant> userSelectedRestaurant; // userId -> selected restaurant

  public Pomato_App() {
    this.restaurantManager = RestaurantManager.getInstance();
    this.orderManager = OrderManager.getInstance();
    this.menuItems = new HashMap<>();
    this.userSelectedRestaurant = new HashMap<>();
    initializeDummyData();
  }

  private void initializeDummyData() {
    // Create restaurants
    Restaurant restaurant1 = new Restaurant("Burger King", "Delhi");
    Restaurant restaurant2 = new Restaurant("Dominos", "Delhi");
    Restaurant restaurant3 = new Restaurant("Pizza Hut", "Mumbai");

    // Add menu items to restaurant1
    MenuItem item1 = new MenuItem("P1", "Chicken Burger", 250);
    MenuItem item2 = new MenuItem("P2", "Veggie Burger", 180);
    restaurant1.addMenuItem(item1);
    restaurant1.addMenuItem(item2);
    menuItems.put("P1", item1);
    menuItems.put("P2", item2);

    // Add menu items to restaurant2
    MenuItem item3 = new MenuItem("P3", "Margherita Pizza", 200);
    MenuItem item4 = new MenuItem("P4", "Pepperoni Pizza", 350);
    restaurant2.addMenuItem(item3);
    restaurant2.addMenuItem(item4);
    menuItems.put("P3", item3);
    menuItems.put("P4", item4);

    // Add restaurants to manager
    restaurantManager.addRestrurent(restaurant1);
    restaurantManager.addRestrurent(restaurant2);
    restaurantManager.addRestrurent(restaurant3);
  }

  public List<Restaurant> searchRestaurants(String location) {
    return restaurantManager.searchByLocation(location);
  }

  public void selectRestaurant(User user, Restaurant restaurant) {
    user.getcart().setRestaurant(restaurant);
    userSelectedRestaurant.put(user.getUserId(), restaurant);
  }

  public void addToCart(User user, String itemId) {
    if (!menuItems.containsKey(itemId)) {
      System.out.println("Item not found: " + itemId);
      return;
    }

    MenuItem item = menuItems.get(itemId);
    user.getcart().addItem(item);
  }

  public void printUserCart(User user) {
    Cart userCart = user.getcart();
    if (userCart.isEmpty()) {
      System.out.println("Cart is empty!");
      return;
    }

    System.out.println("\n--- Cart Contents ---");
    System.out.println("Restaurant: " + userCart.getRestaurant().getName());
    for (MenuItem item : userCart.getItems()) {
      System.out.println("  - " + item.getName() + " ₹" + item.getPrice());
    }
    System.out.println("Total: ₹" + userCart.getTotalCost());
  }

  public Order checkoutNow(User user, String orderType, PaymentStrategy paymentStrategy) {
    Cart cart = user.getcart();
    if (cart.isEmpty()) {
      System.out.println("Cannot checkout. Cart is empty!");
      return null;
    }

    Restaurant restaurant = cart.getRestaurant();
    List<MenuItem> items = cart.getItems();
    double totalCost = cart.getTotalCost();

    // Use NowOrderFactory to create order
    OrderFactory orderFactory = new NowOrderFactory();
    Order order = orderFactory.createOrder(user, cart, restaurant, items, paymentStrategy, totalCost, orderType);

    orderManager.addOrder(order);
    return order;
  }

  public void payForOrder(User user, Order order) {
    if (order == null) {
      System.out.println("Invalid order!");
      return;
    }

    if (order.processPayment()) {
      // Send notification after successful payment
      NotificationService.notify(order);
      // Clear the cart after successful payment
      user.getcart().clear();
    }
  }
}
