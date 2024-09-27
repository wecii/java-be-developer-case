package org.byesildal.inghubsbe.data.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawEvent {
    public WithdrawEvent(BigDecimal amount, String walletId){
        this.amount = amount;
        this.walletId = walletId;
    }
    private BigDecimal amount;
    private String walletId;
    private String type = "withdrawal";
}
