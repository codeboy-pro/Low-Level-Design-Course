package Pomato.factories;
import java.util.List;

import Pomato.models.Cart;
import Pomato.models.MenuItem;
import Pomato.models.Order;
import Pomato.models.Restaurant;
import Pomato.models.User;
import Pomato.strategies.PaymentStrategy;
public interface OrderFactory {
  Order createOrder(User user,Cart cart,Restaurant restaurant,List<MenuItem>menuItems,PaymentStrategy paymentStrategy,double totalCost,String orderType);
}
