package org.byesildal.inghubsbe.data.service;

import org.byesildal.inghubsbe.data.entity.Asset;

import java.util.List;

public interface AssetService {
    Asset getById(String assetId);
    Asset getByName(String name);
    List<Asset> getAll();
    Asset create(String assetName);
}
