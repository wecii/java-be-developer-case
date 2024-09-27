package org.byesildal.inghubsbe.data.repository;

import org.byesildal.inghubsbe.data.entity.Order;
import org.byesildal.inghubsbe.data.entity.User;
import org.byesildal.inghubsbe.data.enums.OrderStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {
    Order save(Order order);
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus status);
}
