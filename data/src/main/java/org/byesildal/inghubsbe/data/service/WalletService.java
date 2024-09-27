package org.byesildal.inghubsbe.data.service;

import org.byesildal.inghubsbe.data.entity.Asset;
import org.byesildal.inghubsbe.data.entity.User;
import org.byesildal.inghubsbe.data.entity.Wallet;

import java.util.List;

public interface WalletService {
    Wallet getByUserAndAsset(User user, Asset asset);
    Wallet save(Wallet wallet);
    Wallet getById(String id);
    List<Wallet> getAll();
    List<Wallet> getByUser(User user);
}
