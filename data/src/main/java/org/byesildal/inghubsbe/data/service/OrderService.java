package org.byesildal.inghubsbe.data.service;

import org.byesildal.inghubsbe.data.entity.Order;
import org.byesildal.inghubsbe.data.entity.User;

import java.util.List;

public interface OrderService {
    Order save(Order order);
    List<Order> getByUser(User user);
    Order getById(String orderId);
    void cancel(Order order);
    List<Order> getAllActiveOrders();
}
