package org.byesildal.inghubsbe.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.data.entity.Order;
import org.byesildal.inghubsbe.data.entity.User;
import org.byesildal.inghubsbe.data.enums.OrderSide;
import org.byesildal.inghubsbe.data.enums.OrderStatus;
import org.byesildal.inghubsbe.data.enums.UserRole;
import org.byesildal.inghubsbe.data.service.WalletService;
import org.byesildal.inghubsbe.data.service.AssetService;
import org.byesildal.inghubsbe.data.service.OrderService;
import org.byesildal.inghubsbe.data.service.UserService;
import org.byesildal.inghubsbe.order.exception.*;
import org.byesildal.inghubsbe.order.model.OrderRequest;
import org.byesildal.inghubsbe.order.model.OrderResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderMService {

    @Value("${auth-service-uri}")
    private String authServiceUri;

    private final ObjectMapper mapper = new ObjectMapper();

    private final RabbitTemplate rabbitTemplate;
    private final AssetService assetService;
    private final WalletService walletService;
    private final WebClient.Builder webClientBuilder;
    private final OrderService orderService;
    private final UserService userService;
    private final OrderBook orderBook;

    public void manageOrder(String token, OrderRequest orderRequest) throws JsonProcessingException {
        var asset = assetService.getById(orderRequest.getAssetId());
        if (asset == null)
            throw new AssetNotExistException("Currency not found");

        var user = getUserByToken(token);
        if (user == null)
            throw new UserNotExistException("User not found");

        var wallet = walletService.getByUserAndAsset(user, asset);
        if (wallet == null)
            throw new AssetNotExistException("Asset not found");

        var side = OrderSide.valueOf(orderRequest.getSide());
        var tryAsset = assetService.getByName("TRY");
        var tryWallet = walletService.getByUserAndAsset(user, tryAsset);
        if (tryWallet == null)
            throw new WalletNotExistException("TRY Wallet not found");

        if (side.equals(OrderSide.BUY) && tryWallet.getUsableBalance().compareTo(orderRequest.getPrice().multiply(orderRequest.getSize())) < 0)
            throw new InsufficientBalanceException("Insufficient balance to buy order");

        if (side.equals(OrderSide.SELL) && wallet.getUsableBalance().compareTo(orderRequest.getSize()) < 0)
            throw new InsufficientBalanceException("Insufficient balance to sell order");

        if (side.equals(OrderSide.SELL)) {
            var newUsableBalance = wallet.getUsableBalance().subtract(orderRequest.getSize());
            wallet.setUsableBalance(newUsableBalance);
        }

        if (side.equals(OrderSide.BUY)) {
            var newUsableTryBalance = tryWallet.getUsableBalance().subtract(orderRequest.getPrice().multiply(orderRequest.getSize()));
            tryWallet.setUsableBalance(newUsableTryBalance);
        }

        var savedWallet = walletService.save(wallet);
        var savedTryWallet = walletService.save(tryWallet);

        var order = new Order();
        order.setUser(user);
        order.setWallet(savedWallet);
        order.setSide(side);
        order.setSize(orderRequest.getSize());
        order.setPrice(orderRequest.getPrice());
        order.setStatus(OrderStatus.PENDING);
        orderService.save(order);
    }

    public void sendCancelOrder(String token, String orderId) {
        var user = getUserByToken(token);
        if (user == null)
            throw new UserNotExistException("User not found");

        var order = orderService.getById(orderId);
        orderService.cancel(order);
    }

    public Map<String, List<OrderResponse>> match(String token){
        var user = getUserByToken(token);
        if (user == null || !user.getRole().equals(UserRole.ADMIN))
            throw new ForbiddenException("Forbidden");

        return orderBook.matchOrders()
                .entrySet()
                .stream()
                .collect(Collectors
                        .toMap(
                                Map.Entry::getKey,
                                e-> e.getValue()
                                        .stream()
                                        .map(OrderResponse::new)
                                        .toList()
                        )
                );
    }

    public List<Order> list(String token){
        var user = getUserByToken(token);
        return orderService.getByUser(user);
    }

    public String extractUsername(String token){
        WebClient client = webClientBuilder.build();
        var responseMono = client.get()
                .uri(authServiceUri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(LinkedHashMap.class);
        var map = (HashMap) responseMono.block().get("data");
        return (String) map.get("username");
    }

    public boolean isAdmin(String token){
        var email = extractUsername(token);
        var user = userService.getByEmail(email);
        if (user.isEmpty())
            throw new UserNotExistException("User not found");

        return user.get().getRole().equals(UserRole.ADMIN);
    }

    public User getUserByToken(String token){
        var email = extractUsername(token);
        var user = userService.getByEmail(email);
        if (user.isEmpty())
            throw new UserNotExistException("User not found");

        return user.get();
    }

    public User getUserByUUID(String uuid){
        var user = userService.getByUuid(UUID.fromString(uuid));
        if (user.isEmpty())
            throw new UserNotExistException("User not found");
        return user.get();
    }
}
