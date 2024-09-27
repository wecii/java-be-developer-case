package org.byesildal.inghubsbe.data.repository;

import org.byesildal.inghubsbe.data.entity.Asset;
import org.byesildal.inghubsbe.data.entity.User;
import org.byesildal.inghubsbe.data.entity.Wallet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends CrudRepository<Wallet, UUID> {
    Optional<Wallet> findByUserAndAsset(User user, Asset asset);
    List<Wallet> findByUser(User user);
}
