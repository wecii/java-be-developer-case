package org.byesildal.inghubsbe.data.service;

import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.data.entity.Asset;
import org.byesildal.inghubsbe.data.entity.User;
import org.byesildal.inghubsbe.data.entity.Wallet;
import org.byesildal.inghubsbe.data.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    @Override
    public Wallet getByUserAndAsset(User user, Asset asset) {
        return walletRepository.findByUserAndAsset(user, asset).orElse(null);
    }

    @Override
    public Wallet save(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet getById(String id) {
        return walletRepository.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public List<Wallet> getAll() {
        var assets = walletRepository.findAll();
        return StreamSupport.stream(assets.spliterator(), false).toList();
    }

    @Override
    public List<Wallet> getByUser(User user) {
        return walletRepository.findByUser(user);
    }
}
