package org.byesildal.inghubsbe.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.byesildal.inghubsbe.data.enums.OrderSide;
import org.byesildal.inghubsbe.data.enums.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
@Entity
public class Order extends Base implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(name = "side")
    private OrderSide side;

    @Column(name = "size", nullable = false)
    private BigDecimal size;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;
}
