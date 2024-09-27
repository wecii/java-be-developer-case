package org.byesildal.inghubsbe.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.byesildal.inghubsbe.data.entity.Wallet;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    public WalletResponse(Wallet wallet){
        this.id = wallet.getId().toString();
        this.balance = wallet.getBalance();
        this.usableBalance = wallet.getUsableBalance();
        this.asset = new AssetResponse(wallet.getAsset().getId().toString(), wallet.getAsset().getName());
    }
    public String id;
    private BigDecimal balance;
    private BigDecimal usableBalance;
    private AssetResponse asset;
}
