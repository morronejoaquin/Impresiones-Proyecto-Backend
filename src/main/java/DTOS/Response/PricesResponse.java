package DTOS.Response;
import java.util.UUID;

import lombok.Data;

@Data
public class PricesResponse {
    private UUID id;
    private double pricePerSheetBW;
    private double pricePerSheetColor;
    private double priceRingedBinding;
}

