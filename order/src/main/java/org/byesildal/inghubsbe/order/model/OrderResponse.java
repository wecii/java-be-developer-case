package org.byesildal.inghubsbe.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.byesildal.inghubsbe.data.entity.Order;
import org.byesildal.inghubsbe.data.enums.OrderSide;
import org.byesildal.inghubsbe.data.enums.OrderStatus;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    public OrderResponse(Order order) {
        this.assetId = order.getWallet().getAsset().getId().toString();
        this.orderId = order.getId().toString();
        this.side = order.getSide();
        this.size = order.getSize();
        this.price = order.getPrice();
        this.status = order.getStatus();
    }
    private String assetId;
    private String orderId;
    private OrderSide side;
    private BigDecimal size;
    private BigDecimal price;
    private OrderStatus status;
}
