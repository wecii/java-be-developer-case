package org.byesildal.inghubsbe.transaction.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.byesildal.inghubsbe.data.enums.OrderSide;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotBlank
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Not a valid uuid")
    private String assetId;

    @Pattern(regexp = "BUY|SELL", message = "side must be BUY or SELL")
    private String side;

    @Min(1)
    private BigDecimal size;

    @Min(1)
    private BigDecimal price;
}
