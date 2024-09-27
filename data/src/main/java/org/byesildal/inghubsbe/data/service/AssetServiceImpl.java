package org.byesildal.inghubsbe.data.service;

import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.data.entity.Asset;
import org.byesildal.inghubsbe.data.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    private final AssetRepository assetRepository;
    @Override
    public Asset getById(String assetId) {
        return assetRepository.findById(UUID.fromString(assetId)).orElse(null);
    }

    @Override
    public Asset getByName(String name) {
        return assetRepository.findByName(name);
    }

    @Override
    public List<Asset> getAll() {
        var all = assetRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false).toList();
    }

    @Override
    public Asset create(String assetName) {
        var asset = new Asset();
        asset.setName(assetName);
        return assetRepository.save(asset);
    }
}
