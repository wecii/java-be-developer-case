package org.byesildal.inghubsbe.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositWithdrawResponse {
    private BigDecimal balance;
    private BigDecimal usableBalance;
    private AssetResponse asset;
}
