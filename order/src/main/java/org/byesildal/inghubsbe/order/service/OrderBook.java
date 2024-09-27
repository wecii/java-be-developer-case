package org.byesildal.inghubsbe.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.byesildal.inghubsbe.data.entity.Wallet;
import org.byesildal.inghubsbe.data.entity.Order;
import org.byesildal.inghubsbe.data.enums.OrderSide;
import org.byesildal.inghubsbe.data.enums.OrderStatus;
import org.byesildal.inghubsbe.data.repository.OrderRepository;
import org.byesildal.inghubsbe.data.service.WalletService;
import org.byesildal.inghubsbe.data.service.OrderService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderBook {

    private final OrderService orderService;
    private final WalletService walletService;

    final List<Order> buyOrders = new ArrayList<>();
    final List<Order> sellOrders = new ArrayList<>();

    private final OrderRepository orderRepository; // Veritabanı bağlantısı

    public void addOrder(Order order) {
        if (order.getSide() == OrderSide.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
    }

    public Map<String, List<Order>> matchOrders() {
        // Sıralama: Buy orders descending by price, Sell orders ascending by price
        buyOrders.sort(Comparator.comparing(Order::getPrice).reversed());
        sellOrders.sort(Comparator.comparing(Order::getPrice));

        List<Order> matchedOrders = new ArrayList<>();
        List<Order> matchedBuyOrders = new ArrayList<>();
        List<Order> matchedSellOrders = new ArrayList<>();

        Iterator<Order> buyIterator = buyOrders.iterator();
        Iterator<Order> sellIterator = sellOrders.iterator();

        while (buyIterator.hasNext() && sellIterator.hasNext()) {
            Order buyOrder = buyIterator.next();
            Order sellOrder = sellIterator.next();

            // Döviz birimi eşleşiyor mu kontrolü
            if (!buyOrder.getWallet().getAsset().equals(sellOrder.getWallet().getAsset())) {
                continue; // Eşleşme yok, devam et
            }

            // Alım fiyatı satım fiyatına eşit veya büyük mü?
            if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0) {
                // Veritabanından güncel Asset nesnelerini al
                Wallet buyWallet = walletService.getById(buyOrder.getWallet().getId().toString());
                Wallet sellWallet = walletService.getById(sellOrder.getWallet().getId().toString());

                // Büyüklük kontrolü: Tam eşleşme için
                if (buyOrder.getSize().compareTo(sellOrder.getSize()) == 0) {
                    // Bakiyeleri kontrol et
                    BigDecimal totalBuyCost = buyOrder.getSize().multiply(buyOrder.getPrice());
                    BigDecimal totalSellEarnings = sellOrder.getSize().multiply(sellOrder.getPrice());

                    // Alıcı bakiyesi kontrolü
                    if (buyWallet.getBalance().compareTo(totalBuyCost) < 0) {
                        continue; // Yeterli bakiye yok, devam et
                    }

                    // Satıcı varlık kontrolü
                    if (sellWallet.getBalance().compareTo(sellOrder.getSize()) < 0) {
                        continue; // Yeterli varlık yok, devam et
                    }

                    // Bakiyeleri güncelle
                    buyWallet.setBalance(buyWallet.getBalance().subtract(totalBuyCost));
                    sellWallet.setBalance(sellWallet.getBalance().add(totalSellEarnings));

                    // Asset'leri kaydet
                    walletService.save(buyWallet);
                    walletService.save(sellWallet);

                    // Emirlerin durumunu güncelle
                    buyOrder.setStatus(OrderStatus.MATCHED);
                    sellOrder.setStatus(OrderStatus.MATCHED);

                    // Emirleri kaydet
                    orderService.save(buyOrder);
                    orderService.save(sellOrder);

                    // Eşleşen emirleri listeye ekle
                    matchedOrders.add(buyOrder);
                    matchedOrders.add(sellOrder);

                    matchedBuyOrders.add(buyOrder);
                    matchedSellOrders.add(sellOrder);

                    // Eşleşen emirleri buy ve sell listelerinden çıkar
                    buyIterator.remove();
                    sellIterator.remove();
                }
            }
        }

        // Kalan emirler
        Map<String, List<Order>> result = new HashMap<>();
        result.put("buyOrders", buyOrders);
        result.put("sellOrders", sellOrders);
        return result;
    }

    public void cancelOrder(String orderId) {
        // İptal edilecek emirleri bul
        Iterator<Order> buyIterator = buyOrders.iterator();
        while (buyIterator.hasNext()) {
            Order buyOrder = buyIterator.next();
            if (buyOrder.getId().toString().equals(orderId) && buyOrder.getStatus() == OrderStatus.PENDING) {
                orderService.cancel(buyOrder);
                buyIterator.remove(); // Listeden kaldır
                log.info("Cancelled buy order: {}", buyOrder);
            }
        }

        Iterator<Order> sellIterator = sellOrders.iterator();
        while (sellIterator.hasNext()) {
            Order sellOrder = sellIterator.next();
            if (sellOrder.getId().toString().equals(orderId) && sellOrder.getStatus() == OrderStatus.PENDING) {
                orderService.cancel(sellOrder);
                sellIterator.remove(); // Listeden kaldır
                log.info("Cancelled sell order: {}", sellOrder);
            }
        }
    }

}