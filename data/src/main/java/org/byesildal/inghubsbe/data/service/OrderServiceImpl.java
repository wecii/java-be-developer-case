package org.byesildal.inghubsbe.data.service;

import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.data.entity.Order;
import org.byesildal.inghubsbe.data.entity.User;
import org.byesildal.inghubsbe.data.enums.OrderStatus;
import org.byesildal.inghubsbe.data.repository.WalletRepository;
import org.byesildal.inghubsbe.data.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final WalletRepository walletRepository;

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public Order getById(String orderId) {
        return orderRepository.findById(UUID.fromString(orderId)).orElse(null);
    }

    @Override
    public void cancel(Order order) {
        var wallet = order.getWallet();
        var usable = wallet.getUsableBalance();
        usable = usable.add(order.getPrice());
        wallet.setUsableBalance(usable);
        walletRepository.save(wallet);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public List<Order> getAllActiveOrders() {
        return orderRepository.findByStatus(OrderStatus.PENDING);
    }
}
