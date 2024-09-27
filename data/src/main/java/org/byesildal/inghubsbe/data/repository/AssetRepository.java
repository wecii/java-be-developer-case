package org.byesildal.inghubsbe.data.repository;

import org.byesildal.inghubsbe.data.entity.Asset;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AssetRepository extends CrudRepository<Asset, UUID> {
    Asset findByName(String name);
}
