package org.byesildal.inghubsbe.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.byesildal.inghubsbe.data.entity.Asset;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponse {
    public AssetResponse(Asset asset) {
        this.id = asset.getId().toString();
        this.name = asset.getName();
    }
    private String id;
    private String name;
}
