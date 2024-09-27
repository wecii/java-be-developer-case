package org.byesildal.inghubsbe.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositEvent {
    public DepositEvent(BigDecimal amount, String walletId){
        this.amount = amount;
        this.walletId = walletId;
    }
    private BigDecimal amount;
    private String walletId;
    private String type = "deposit";
}
