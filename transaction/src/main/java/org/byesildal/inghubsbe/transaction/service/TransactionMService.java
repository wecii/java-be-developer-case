package org.byesildal.inghubsbe.transaction.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.byesildal.inghubsbe.data.entity.Order;
import org.byesildal.inghubsbe.data.entity.User;
import org.byesildal.inghubsbe.data.enums.OrderSide;
import org.byesildal.inghubsbe.data.enums.OrderStatus;
import org.byesildal.inghubsbe.data.enums.UserRole;
import org.byesildal.inghubsbe.data.model.DepositEvent;
import org.byesildal.inghubsbe.data.model.WithdrawEvent;
import org.byesildal.inghubsbe.data.service.AssetService;
import org.byesildal.inghubsbe.data.service.OrderService;
import org.byesildal.inghubsbe.data.service.UserService;
import org.byesildal.inghubsbe.data.service.WalletService;
import org.byesildal.inghubsbe.transaction.exception.ForbiddenException;
import org.byesildal.inghubsbe.transaction.exception.UserNotExistException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionMService {

    @Value("${auth-service-uri}")
    private String authServiceUri;

    private final UserService userService;
    private final WalletService walletService;
    private final AssetService assetService;
    private final OrderService orderService;
    private final WebClient.Builder webClientBuilder;

    // Orderbook
    private Map<String, List<Order>> matchByAsset (List<Order> orders){
        log.info("Orderbook processing started, {} ", orders.get(0).getWallet().getAsset().getName());

        List<Order> matchedOrders = new ArrayList<>();

        PriorityQueue<Order> buyOrders = new PriorityQueue<>((o1, o2) -> o2.getPrice().compareTo(o1.getPrice()));
        PriorityQueue<Order> sellOrders = new PriorityQueue<>(Comparator.comparing(Order::getPrice));

        for (var order : orders){
            if (order.getSide() == OrderSide.BUY) {
                buyOrders.offer(order);
            } else {
                sellOrders.offer(order);
            }
        }

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.poll();
            Order sellOrder = sellOrders.poll();

            if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0) {

                BigDecimal matchedSize = buyOrder.getSize().compareTo(sellOrder.getSize()) < 0 ? buyOrder.getSize() : sellOrder.getSize();

                var buyAssetWallet = walletService.getByUserAndAsset(buyOrder.getUser(), buyOrder.getWallet().getAsset());
                var buyTryWallet = walletService.getByUserAndAsset(buyOrder.getUser(), assetService.getByName("TRY"));

                var buyAssetNewBalance = buyAssetWallet.getBalance().add(matchedSize);
                buyAssetWallet.setBalance(buyAssetNewBalance);
                var buyAssetUsableBalance = buyAssetWallet.getUsableBalance().add(matchedSize);
                buyAssetWallet.setUsableBalance(buyAssetUsableBalance);
                var savedBuyAssetWallet = walletService.save(buyAssetWallet);

                var buyTryNewBalance = buyTryWallet.getBalance().subtract(buyOrder.getPrice().multiply(matchedSize));
                buyTryWallet.setBalance(buyTryNewBalance);
                walletService.save(buyTryWallet);

                var sellAssetWallet = walletService.getByUserAndAsset(sellOrder.getUser(), sellOrder.getWallet().getAsset());
                var sellTryWallet = walletService.getByUserAndAsset(sellOrder.getUser(), assetService.getByName("TRY"));

                var sellAssetNewBalance = sellAssetWallet.getBalance().subtract(matchedSize);
                sellAssetWallet.setBalance(sellAssetNewBalance);
                var savedSellAssetWallet = walletService.save(sellAssetWallet);

                var sellTryNewBalance = sellTryWallet.getBalance().add(sellOrder.getPrice().multiply(matchedSize));
                sellTryWallet.setBalance(sellTryNewBalance);
                var sellTryNewUsableBalance = sellTryWallet.getUsableBalance().add(sellOrder.getPrice().multiply(matchedSize));
                sellTryWallet.setUsableBalance(sellTryNewUsableBalance);
                walletService.save(sellTryWallet);

                buyOrder.setStatus(OrderStatus.MATCHED);
                sellOrder.setStatus(OrderStatus.MATCHED);
                orderService.save(buyOrder);
                orderService.save(sellOrder);

                matchedOrders.add(buyOrder);
                matchedOrders.add(sellOrder);

                if (buyOrder.getSize().compareTo(matchedSize) > 0) {
                    Order newBuyOrder = new Order();
                    newBuyOrder.setStatus(OrderStatus.PENDING);
                    newBuyOrder.setSize(buyOrder.getSize().subtract(matchedSize));
                    newBuyOrder.setPrice(buyOrder.getPrice());
                    newBuyOrder.setWallet(savedBuyAssetWallet);
                    newBuyOrder.setUser(buyOrder.getUser());
                    newBuyOrder.setSide(OrderSide.BUY);
                    orderService.save(newBuyOrder);
                }

                if (sellOrder.getSize().compareTo(matchedSize) > 0) {
                    Order newSellOrder = new Order();
                    newSellOrder.setStatus(OrderStatus.PENDING);
                    newSellOrder.setSize(sellOrder.getSize().subtract(matchedSize));
                    newSellOrder.setPrice(sellOrder.getPrice());
                    newSellOrder.setWallet(savedSellAssetWallet);
                    newSellOrder.setUser(sellOrder.getUser());
                    newSellOrder.setSide(OrderSide.SELL);
                    orderService.save(newSellOrder);
                }

            } else {
                buyOrders.offer(buyOrder);
                sellOrders.offer(sellOrder);
            }
        }

        buyOrders.removeAll(matchedOrders);
        sellOrders.removeAll(matchedOrders);

        Map<String, List<Order>> result = new HashMap<>();
        result.put("matched", matchedOrders);
        return result;
    }

    public List<Map<String, List<Order>>> match(String token){
        var user = getUserByToken(token);
        if (!user.getRole().equals(UserRole.ADMIN))
            throw new ForbiddenException("Forbidden");

        var orders = orderService.getAllActiveOrders();
        return orders
                .stream()
                .collect(Collectors.groupingBy(o->o.getWallet().getAsset().getName()))
                .values()
                .stream()
                .map(this::matchByAsset)
                .toList();
    }

    public void deposit(DepositEvent depositEvent){
        var wallet = walletService.getById(depositEvent.getWalletId());
        if (wallet == null) {
            log.error("Wallet Not Found");
            return;
        }

        var total = wallet.getBalance();
        total = total.add(depositEvent.getAmount());

        var usable = wallet.getUsableBalance();
        usable = usable.add(depositEvent.getAmount());

        wallet.setBalance(total);
        wallet.setUsableBalance(usable);
        walletService.save(wallet);
    }

    public void withdraw(WithdrawEvent withdrawEvent){
        var wallet = walletService.getById(withdrawEvent.getWalletId());
        if (wallet == null) {
            log.error("Wallet Not Found");
            return;
        }

        var total = wallet.getBalance();
        total = total.subtract(withdrawEvent.getAmount());

        var usable = wallet.getUsableBalance();
        usable = usable.subtract(withdrawEvent.getAmount());

        var belowLimitTotal = total.compareTo(new BigDecimal(0)) < 0;
        var belowLimitUsable = usable.compareTo(new BigDecimal(0)) < 0;
        if (belowLimitUsable || belowLimitTotal)
            log.error("Insufficient balance");

        wallet.setBalance(total);
        wallet.setUsableBalance(usable);
        walletService.save(wallet);
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
